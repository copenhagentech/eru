package app.dtos.responses;

public record AuthResponseDTO(String token, Integer userId, String username) {
}
