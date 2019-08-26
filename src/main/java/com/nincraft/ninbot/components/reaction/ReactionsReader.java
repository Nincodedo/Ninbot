package com.nincraft.ninbot.components.reaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ReactionsReader {

    private ObjectMapper objectMapper;

    public ReactionsReader(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    private static List<String> badCharacters = Arrays.asList(" ", "!", ":");

    @Bean
    List<ReactionResponse> reactionResponseList() {
        try {

            String jsonString = readFromInputStream(getClass().getClassLoader()
                    .getResourceAsStream("responses.json"));
            List<ReactionResponse> reactionResponseList = objectMapper.readValue(objectMapper.readTree(jsonString)
                    .get("responses")
                    .toString(), objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, ReactionResponse.class));
            return reactionResponseList.stream()
                    .map(this::generateResponse)
                    .sorted(Comparator.comparing(ReactionResponse::getType).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to load reaction responses JSON", e);
        }
        return new ArrayList<>();
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    private ReactionResponse generateResponse(ReactionResponse response) {
        if (isCanEmoji(response.getResponse())) {
            return new EmojiReactionResponse(response);
        } else {
            return new StringReactionResponse(response);
        }
    }

    private boolean isCanEmoji(String response) {
        if (containsBadCharacters(response)) {
            return false;
        }
        for (char c : response.toCharArray()) {
            String check = StringUtils.replaceOnce(response, Character.toString(c), "");
            if (check.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsBadCharacters(String response) {
        return badCharacters.stream().anyMatch(response::contains);
    }
}
