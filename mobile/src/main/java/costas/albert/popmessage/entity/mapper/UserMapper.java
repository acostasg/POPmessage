package costas.albert.popmessage.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;

import costas.albert.popmessage.entity.User;

public class UserMapper {
    public static User build(byte[] responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseBody, User.class);
    }

    public static User build(String responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseBody, User.class);
    }
}