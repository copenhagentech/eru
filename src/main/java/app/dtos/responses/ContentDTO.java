package app.dtos.responses;

import app.entities.Content;
import app.entities.enums.ContentType;

import java.time.LocalDateTime;

public record ContentDTO(
        Integer id,
        String title,
        String body,
        ContentType contentType,
        String category,
        String source,
        String author,
        boolean active,
        LocalDateTime createdAt
) {
    public static ContentDTO fromEntity(Content content) {
        return new ContentDTO(
                content.getId(),
                content.getTitle(),
                content.getBody(),
                content.getContentType(),
                content.getCategory(),
                content.getSource(),
                content.getAuthor(),
                content.isActive(),
                content.getCreatedAt()
        );
    }
}
