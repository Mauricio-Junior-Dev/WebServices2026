package senac.dws.veiculos.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ApiErrorResponse",
        description = "Corpo JSON padrão retornado pelo tratamento global de erros ou pelo filtro de rate limiting. O campo \"status\" espelha o código HTTP real da resposta (400, 404, 409, 429, 500, etc.).")
public class ApiErrorResponse {

    @Schema(description = "Data e hora do erro no servidor", example = "2026-04-07T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP da resposta", example = "404")
    private int status;

    @Schema(description = "Razão breve do status HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem descritiva para o cliente", example = "Recurso não encontrado.")
    private String message;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
