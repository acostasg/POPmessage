package costas.albert.popmessage.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;

import costas.albert.popmessage.entity.Vote;

public class VoteMapper {
    public static Vote build(byte[] responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseBody, Vote.class);
    }
}