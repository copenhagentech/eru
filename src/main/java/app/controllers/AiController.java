package app.controllers;

import app.dtos.requests.ElaborateRequestDTO;
import app.dtos.responses.ElaborateResponseDTO;
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
        ElaborateRequestDTO request = ctx.bodyAsClass(ElaborateRequestDTO.class);
        logger.info("AI elaborate request titleLength={} bodyLength={}",
                request.title() != null ? request.title().length() : 0,
                request.body() != null ? request.body().length() : 0);

        String explanation = openAiService.elaborateContent(
                request.title(),
                request.body()
        );

        ctx.json(new ElaborateResponseDTO(explanation));
    }
}
