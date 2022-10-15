package dev.nincodedo.ninbot.components.reaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
class ReactionsReader {

    @Bean
    List<ReactionResponse> reactionResponseList() {
        try {
            String jsonString = readFromInputStream(new ClassPathResource("responses.json").getInputStream());
            List<ReactionResponse> reactionResponseList = new ArrayList<>();
            for (ReactionMatchType type : ReactionMatchType.values()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ReactionResponse> typeReaction = objectMapper.readValue(objectMapper.readTree(jsonString)
                        .get("responses")
                        .get(type.getName())
                        .toString(), objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ReactionResponse.class));
                typeReaction.forEach(reactionResponse -> reactionResponse.setReactionMatchType(type));
                reactionResponseList.addAll(typeReaction);
            }
            return reactionResponseList.stream()
                    .map(this::generateResponse)
                    .toList();
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
        if (ReactionUtils.hasSpecialActionsActions(response.getResponse())) {
            return new SpecialReactionResponse(response);
        } else if (ReactionUtils.isCanEmoji(response.getResponse())) {
            return new EmojiReactionResponse(response);
        } else {
            return new StringReactionResponse(response);
        }
    }
}
