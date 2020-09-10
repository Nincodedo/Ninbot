package dev.nincodedo.ninbot.components.trivia.game.opentdb;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.nincodedo.ninbot.components.trivia.TriviaInstance;
import dev.nincodedo.ninbot.components.trivia.game.TriviaAPI;
import dev.nincodedo.ninbot.components.trivia.game.TriviaQuestion;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class OpenTDBAPI implements TriviaAPI {

    private static final String HTTP_OPENTDB_COM = "https://opentdb.com/";
    private Map<Integer, String> triviaCategoryMap;
    private List<String> badPhraseList;
    private RestTemplate restTemplate;

    public OpenTDBAPI(RestTemplateBuilder restTemplateBuilder) {
        triviaCategoryMap = new HashMap<>();
        this.badPhraseList = Arrays.asList("which of", "which one of");
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public TriviaQuestion nextTriviaQuestion(TriviaInstance triviaInstance) {
        if (triviaInstance.getApiToken() == null) {
            triviaInstance.setApiToken(getTriviaToken());
        }
        String triviaUrl = buildTriviaUrl(triviaInstance.getApiToken(), triviaInstance.getCategoryId());
        val response = restTemplate.getForObject(triviaUrl, ObjectNode.class);
        if (response != null) {
            val triviaResults = response.get("results").get(0);
            val triviaQuestion = new TriviaQuestion(triviaResults);
            triviaQuestion.unescapeFields();
            return getTriviaQuestion(triviaQuestion);
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
        val getTokenUrl = HTTP_OPENTDB_COM + "api_token.php?command=request";
        TokenResponse tokenResponse = restTemplate.getForObject(getTokenUrl, TokenResponse.class);
        return tokenResponse != null ? tokenResponse.getToken() : "";
    }

    @Override
    public Map<Integer, String> getTriviaCategories() {
        if (!triviaCategoryMap.isEmpty()) {
            return triviaCategoryMap;
        }
        val getCategoriesUrl = HTTP_OPENTDB_COM + "api_category.php";
        TriviaCategoryResponse triviaCategoryResponse = restTemplate.getForObject(getCategoriesUrl,
                TriviaCategoryResponse.class);
        Map<Integer, String> categoryMap = new HashMap<>();
        if (triviaCategoryResponse == null || triviaCategoryResponse.getTriviaCategoryList() == null
                || triviaCategoryResponse.getTriviaCategoryList().isEmpty()) {
            return categoryMap;
        }
        categoryMap = triviaCategoryResponse.getTriviaCategoryList()
                .stream()
                .collect(Collectors.toMap(TriviaCategory::getId, TriviaCategory::getName, (a, b) -> b));
        triviaCategoryMap = categoryMap;
        return triviaCategoryMap;
    }
}
