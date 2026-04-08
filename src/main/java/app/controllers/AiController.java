package app.controllers;

import app.dtos.requests.ElaborateRequestDTO;
import app.dtos.responses.ElaborateResponseDTO;
import app.exceptions.ApiException;
import app.integration.openai.OpenAiService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiController {
    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    private final OpenAiService openAiService;

    public AiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public void elaborate(Context ctx) {
        if (openAiService == null) {
            throw ApiException.configuration("AI integration is disabled. Set OPENAI_API_KEY to use /ai/elaborate");
        }

        ElaborateRequestDTO request = ctx.bodyAsClass(ElaborateRequestDTO.class);
        if (request == null || request.title() == null || request.title().isBlank()
                || request.body() == null || request.body().isBlank()) {
            throw ApiException.badRequest("Both title and body are required");
        }
        logger.info("AI elaborate request titleLength={} bodyLength={}",
                request.title() != null ? request.title().length() : 0,
                request.body() != null ? request.body().length() : 0);

        String explanation;
        try {
            explanation = openAiService.elaborateContent(
                    request.title(),
                    request.body()
            );
        } catch (RuntimeException e) {
            logger.error("AI elaborate failed", e);
            throw ApiException.internal("AI elaboration failed: " + e.getMessage());
        }

        ctx.json(new ElaborateResponseDTO(explanation));
    }
}
