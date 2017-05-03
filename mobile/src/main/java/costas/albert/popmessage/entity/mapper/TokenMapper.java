package costas.albert.popmessage.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import costas.albert.popmessage.entity.Token;

public class TokenMapper {
    public static Token build(byte[] responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject response = new JSONObject(new String(responseBody));
        return mapper.readValue(response.getString("Token"), Token.class);
    }
}