package soaksoak.zippick.zippick.domain.ai.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${gpt.apikey}")
    private String apiKey;
    private String modelId = "gpt-3.5-turbo";
    private String url = "https://api.openai.com/v1/chat/completions";
    private RestTemplate restTemplate = new RestTemplate();


    public String getAnswer(String input) throws ParseException {

        System.out.println(input);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Chat GPT API에 보낼 요청 Body 작성
        String requestBody = "{\"model\": \"" + modelId
                + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + input + "\"}]}";

        // Body를 JSON으로 바꾸기
        JSONParser jsonParser = new JSONParser();

        // 요청 생성
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // Chat GPT API의 응답 내용을 문자열로 바꾸어 리턴
        String result =  restTemplate.postForObject(url, request, String.class);

        JSONObject resultJson = (JSONObject) jsonParser.parse(result);

        JSONArray choices = (JSONArray) resultJson.get("choices");

        JSONObject choice = (JSONObject) choices.get(0);

        JSONObject message =  (JSONObject) choice.get("message");

        return message.get("content").toString();

    }
}