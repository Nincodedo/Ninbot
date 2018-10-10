package com.nincraft.ninbot.components.trivia;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
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
public class TriviaManager {

    private static final String HTTP_OPENTDB_COM = "http://opentdb.com/";
    private TriviaInstanceDao triviaInstanceDao;
    private TriviaScoreService triviaScoreService;
    private Map<Integer, String> triviaCategoryMap;
    private int triviaUnansweredLimit = 3;
    private List<String> badPhraseList;

    public TriviaManager(TriviaInstanceDao triviaInstanceDao, TriviaScoreService triviaScoreService) {
        triviaCategoryMap = new HashMap<>();
        this.triviaInstanceDao = triviaInstanceDao;
        this.triviaScoreService = triviaScoreService;
        this.badPhraseList = Arrays.asList("which of", "which one of");
    }

    private void stopTrivia(TriviaInstance triviaInstance, JDA jda) {
        triviaInstance.getTriviaTimer().cancel();
        jda.getTextChannelById(triviaInstance.getChannelId()).sendMessage(
                "Trivia has ended. No answers from the last " + triviaUnansweredLimit + " questions").queue();
        triviaInstanceDao.removeTriviaInChannel(triviaInstance.getChannelId());
    }

    public void stopTrivia(String channelId) {
        triviaInstanceDao.removeTriviaInChannel(channelId);
    }

    public boolean isTriviaActiveInChannel(String channelId) {
        return triviaInstanceDao.isActiveTriviaChannel(channelId);
    }

    public void startTrivia(String channelId, int categoryId, String serverId, JDA jda) {
        TriviaInstance triviaInstance = new TriviaInstance(serverId, channelId, categoryId);
        val token = getTriviaToken();
        triviaInstance.setApiToken(token);
        triviaInstance.setTriviaQuestion(nextTriviaQuestion(triviaInstance));
        triviaInstanceDao.saveObject(triviaInstance);
        startTriviaLoop(triviaInstance, jda);
    }

    private void startTriviaLoop(TriviaInstance triviaInstance, JDA jda) {
        Thread thread = new Thread(() -> {
            int unansweredQuestionCount = 0;
            while (isTriviaActiveInChannel(triviaInstance.getChannelId())
                    && unansweredQuestionCount <= triviaUnansweredLimit) {
                if (triviaInstance.getTriviaQuestion() != null) {
                    Timer timer = new Timer();
                    triviaInstance.setTriviaTimer(timer);

                    val triviaQuestion = triviaInstance.getTriviaQuestion();
                    triviaInstance.setAnswer(triviaQuestion.getCorrectAnswer().trim());
                    val triviaListener = new TriviaListener(triviaInstance.getChannelId(), triviaInstance.getAnswer(), triviaScoreService);
                    jda.addEventListener(triviaListener);
                    val channel = jda.getTextChannelById(triviaInstance.getChannelId());
                    channel.sendMessage(triviaQuestion.build()).queue();
                    val triviaTask = new TriviaTimeUpTask(triviaInstance, triviaInstanceDao, jda);
                    long time = 30000L;
                    timer.schedule(new TriviaHintTask(triviaInstance, triviaInstanceDao, jda, 1), time);
                    timer.schedule(new TriviaHintTask(triviaInstance, triviaInstanceDao, jda, 2), time * 2);
                    timer.schedule(triviaTask, time * 3);
                    while (!triviaListener.isQuestionAnswered() && !triviaTask.isTimeExpired()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            log.error("oh no", e);
                        }
                    }
                    if (triviaListener.isQuestionAnswered()) {
                        unansweredQuestionCount = 0;
                        timer.cancel();
                    } else if (triviaListener.isSomeAnswerOfSorts()) {
                        unansweredQuestionCount = 0;
                    } else {
                        unansweredQuestionCount++;
                    }
                    jda.removeEventListener(triviaListener);
                    triviaInstance.setTriviaQuestion(null);
                } else {
                    triviaInstance.setTriviaQuestion(nextTriviaQuestion(triviaInstance));
                }
            }
            if (unansweredQuestionCount > triviaUnansweredLimit) {
                stopTrivia(triviaInstance, jda);
            }
        });
        thread.start();
    }

    private TriviaQuestion nextTriviaQuestion(TriviaInstance triviaInstance) {
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
                    if (!containsBadQuestionPhrase(triviaQuestion.getQuestion())) {
                        return triviaQuestion;
                    } else {
                        return null;
                    }
                } else {
                    handleResponseCode(responseCode);
                }
            } catch (IOException e) {
                log.error("Failed to start trivia", e);
            }
        }
        return null;
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

    private void resetToken() {
        String s = HTTP_OPENTDB_COM + "api_token.php?command=reset&token=YOURTOKENHERE";
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

    Map<Integer, String> getTriviaCategories() {
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

    int getPlayerScore(String userId) {
        return triviaScoreService.getPoints(userId);
    }
}
