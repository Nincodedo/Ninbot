package com.nincraft.ninbot.components.fun;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

@Log4j2
@Component
public class UDDefineWordAPI implements DefineWordAPI {

    private String baseUrl = "http://api.urbandictionary.com/v0/define?term=";

    @Override
    public Map<String, String> defineWord(String word) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet(baseUrl + URLEncoder.encode(word, "UTF-8"));

            HttpResponse response = client.execute(get);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Map<String, Object>>() {
            });
            return (Map<String, String>) ((ArrayList) responseMap.get("list")).get(0);

        } catch (IOException e) {
            log.error("HttpGet Failed", e);
        }
        return null;
    }
}
