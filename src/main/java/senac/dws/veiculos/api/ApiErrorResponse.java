package senac.dws.veiculos.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "ApiErrorResponse",
        description = "Corpo JSON padrão retornado pelo tratamento global de erros (ex.: 409 Conflito, 404 Não encontrado).",
        example = """
                {
                  "timestamp": "2026-04-07T14:30:00",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Já existe uma marca cadastrada com este nome."
                }""")
public class ApiErrorResponse {

    @Schema(description = "Data e hora do erro no servidor", example = "2026-04-07T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP", example = "409")
    private int status;

    @Schema(description = "Razão breve do status HTTP", example = "Conflict")
    private String error;

    @Schema(description = "Mensagem descritiva para o cliente", example = "Já existe um país cadastrado com este nome.")
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
