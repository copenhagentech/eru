package app.dtos.requests;

import app.entities.enums.ReactionType;

public record InteractionRequestDTO(ReactionType reactionType) {
}
