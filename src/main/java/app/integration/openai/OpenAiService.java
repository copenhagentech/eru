package app.integration.openai;

import app.entities.Content;

public class OpenAiService {

    private final OpenAiClient openAiClient;

    public OpenAiService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public String elaborate(Content content) {
        return openAiClient.elaborateContent(content.getTitle(), content.getBody());
    }

    public String elaborateContent(String title, String body) {
        return openAiClient.elaborateContent(title, body);
    }
}