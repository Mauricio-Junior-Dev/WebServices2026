# Etapa de build
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas o pom primeiro para aproveitar o cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Imagem de execução (Aqui estava o erro, agora corrigido)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]