package app.dtos.responses;

import app.entities.UserInteraction;
import app.entities.enums.ReactionType;

import java.time.LocalDateTime;

public record InteractionDTO(
        Integer id,
        Integer userId,
        Integer contentId,
        ReactionType reactionType,
        LocalDateTime createdAt
) {
    public static InteractionDTO fromEntity(UserInteraction interaction) {
        return new InteractionDTO(
                interaction.getId(),
                interaction.getUser().getId(),
                interaction.getContent().getId(),
                interaction.getReactionType(),
                interaction.getCreatedAt()
        );
    }
}
