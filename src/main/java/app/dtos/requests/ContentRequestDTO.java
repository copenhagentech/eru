package app.dtos.requests;

import app.entities.enums.ContentType;

public record ContentRequestDTO(
        String title,
        String body,
        ContentType contentType,
        String category,
        String source,
        String author
) {
}
