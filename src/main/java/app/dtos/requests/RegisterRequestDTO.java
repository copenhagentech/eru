package app.dtos.requests;

public record RegisterRequestDTO(
        String firstName,
        String lastName,
        String email,
        String username,
        String password
) {
}
