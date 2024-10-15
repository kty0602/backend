package jpabook.trello_project.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlackNotifier {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Async
    public void sendSlackMessage(String message) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> payload = new HashMap<String, String>();
        payload.put("text", message);

        restTemplate.postForObject(webhookUrl, payload, String.class);
    }
}
