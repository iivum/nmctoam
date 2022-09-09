package site.iivum.ncmtoam.netease.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import site.iivum.ncmtoam.netease.model.Result;

/**
 * 2020/3/8
 *
 * @author lbh
 */
public class ResultHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static Result handle(String response) {
        return objectMapper.readValue(response, Result.class);
    }
}
