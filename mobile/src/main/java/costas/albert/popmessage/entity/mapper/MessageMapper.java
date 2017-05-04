package costas.albert.popmessage.entity.mapper;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import costas.albert.popmessage.entity.Message;

public class MessageMapper {

    public static Message build(byte[] responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseBody, Message.class);
    }

    @NonNull
    public static List<Message> buildList(byte[] responseBody) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(responseBody, Message[].class));
    }
}