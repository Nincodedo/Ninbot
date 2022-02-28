package dev.nincodedo.ninbot.components.define;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UDDefineWordAPI implements DefineWordAPI {

    @Override
    public Word defineWord(String word) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            String baseUrl = "http://api.urbandictionary.com/v0/define?term=";
            HttpGet get = new HttpGet(baseUrl + URLEncoder.encode(word, StandardCharsets.UTF_8));

            HttpResponse response = client.execute(get);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()),
                    new TypeReference<>() {
                    });
            List responseList = (ArrayList) responseMap.get("list");
            if (!responseList.isEmpty()) {
                var responseListMap = (Map<String, String>) responseList.get(0);
                return new Word(word, responseListMap.get("definition"), responseListMap.get("permalink"));
            } else {
                return null;
            }

        } catch (IOException e) {
            log.error("HttpGet Failed", e);
        }
        return null;
    }
}
