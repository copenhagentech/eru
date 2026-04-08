package app.dtos.responses;

import app.dtos.internal.AuthenticatedUserDTO;

import java.util.Set;

public record CurrentUserDTO(
        Integer userId,
        String username,
        Set<String> roles
) {
    public static CurrentUserDTO fromAuthenticatedUser(AuthenticatedUserDTO user) {
        return new CurrentUserDTO(user.userId(), user.username(), user.roles());
    }
}
