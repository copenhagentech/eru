package app.dtos.internal;

import java.util.Set;

public record AuthenticatedUserDTO(
        Integer userId,
        String username,
        Set<String> roles
) {
}
