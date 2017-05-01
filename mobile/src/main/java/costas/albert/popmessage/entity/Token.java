package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

@JsonPropertyOrder({
        "hash"
})
public class Token {

    @JsonProperty("hash")
    public String hash;

    public Token(){}

    public Token(String hash) {
        this.hash = hash;
    }

    public String hash() {
        return this.hash;
    }

    public boolean isEmpty() {
        return this.hash.isEmpty();
    }

    public static Token build(byte[] responseBody) throws java.io.IOException, org.json.JSONException {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject response = new JSONObject(new String(responseBody));
        return mapper.readValue(response.getString("Token"), Token.class);
    }

}
