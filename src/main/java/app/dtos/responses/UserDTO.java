package app.dtos.responses;

import app.entities.User;

import java.time.LocalDateTime;

public record UserDTO(
        Integer id,
        String username,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
