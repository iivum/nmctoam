package site.iivum.ncmtoam.netease.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import site.iivum.ncmtoam.netease.model.Result;

/**
 * 2020/3/8
 *
 * @author lbh
 */
public class ResultHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result handle(String response) throws JsonProcessingException {
        return objectMapper.readValue(response, Result.class);
    }
}
