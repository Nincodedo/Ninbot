package com.nincraft.ninbot.components.trivia.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nincraft.ninbot.components.trivia.TriviaInstance;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Log4j2
@Component
public class OpenTDBAPI implements TriviaAPI {

    private static final String HTTP_OPENTDB_COM = "http://opentdb.com/";
    private Map<Integer, String> triviaCategoryMap;
    private List<String> badPhraseList;

    public OpenTDBAPI() {
        triviaCategoryMap = new HashMap<>();
        this.badPhraseList = Arrays.asList("which of", "which one of");
    }

    @Override
    public TriviaQuestion nextTriviaQuestion(TriviaInstance triviaInstance) {
        if (triviaInstance.getApiToken() == null) {
            triviaInstance.setApiToken(getTriviaToken());
        }
        String triviaUrl = buildTriviaUrl(triviaInstance.getApiToken(), triviaInstance.getCategoryId());
        val jsonOptional = httpGetJson(triviaUrl);
        if (jsonOptional.isPresent()) {
            try {
                val json = jsonOptional.get();
                ObjectMapper objectMapper = new ObjectMapper();
                val jsonTree = objectMapper.readTree(json);
                val responseCode = jsonTree.get("response_code").asInt();
                if (responseCode == 0) {
                    val triviaResults = jsonTree.get("results").get(0).toString();
                    val triviaQuestion = objectMapper.readValue(triviaResults, TriviaQuestion.class);
                    triviaQuestion.unescapeFields();
                    return getTriviaQuestion(triviaQuestion);
                } else {
                    handleResponseCode(responseCode);
                }
            } catch (IOException e) {
                log.error("Failed to start trivia", e);
            }
        }
        return null;
    }

    private TriviaQuestion getTriviaQuestion(TriviaQuestion triviaQuestion) {
        if (!containsBadQuestionPhrase(triviaQuestion.getQuestion())) {
            return triviaQuestion;
        } else {
            return null;
        }
    }

    private boolean containsBadQuestionPhrase(String question) {
        val lowerQuestion = question.toLowerCase();
        for (val badPhrase : badPhraseList) {
            if (lowerQuestion.contains(badPhrase.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void handleResponseCode(int responseCode) {
        switch (responseCode) {
            case 1:
                log.error("No results from API");
                break;
            case 2:
                log.error("Invalid parameter");
                break;
            case 3:
                log.error("Session token not found, generate a new one");
                break;
            case 4:
                log.error("Session token has run out of trivia");
                break;
            default:
                log.error("Unknown response code");
                break;
        }
    }

    private Optional<String> httpGetJson(String getUrl) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(getUrl);
            HttpResponse response = client.execute(get);
            return Optional.of(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error("HttpGet Failed", e);
        }
        return Optional.empty();
    }

    private String buildTriviaUrl(String token, int categoryId) {
        String triviaUrl = HTTP_OPENTDB_COM + "api.php?amount=1&type=multiple";
        if (!token.isEmpty()) {
            triviaUrl += "&token=" + token;
        }
        if (categoryId != 0) {
            triviaUrl += "&category=" + categoryId;
        }
        return triviaUrl;
    }

    private String getTriviaToken() {
        val jsonOptional = httpGetJson(HTTP_OPENTDB_COM + "api_token.php?command=request");
        if (jsonOptional.isPresent()) {
            val json = jsonOptional.get();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });
                if (((Integer) map.get("response_code")) == 0) {
                    return (String) map.get("token");
                }
            } catch (IOException e) {
                log.warn("Failed to get trivia token, continuing without one", e);
            }
        }
        return "";
    }

    @Override
    public Map<Integer, String> getTriviaCategories() {
        if (!triviaCategoryMap.isEmpty()) {
            return triviaCategoryMap;
        }
        val jsonOptional = httpGetJson(HTTP_OPENTDB_COM + "api_category.php");
        if (jsonOptional.isPresent()) {
            val json = jsonOptional.get();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.readTree(json).get("trivia_categories").forEach(jsonNode -> triviaCategoryMap.put(jsonNode.get("id").asInt(), jsonNode.get("name").asText()));
            } catch (IOException e) {
                log.error("Unable to read trivia response", e);
            }
        }
        return triviaCategoryMap;
    }
}
