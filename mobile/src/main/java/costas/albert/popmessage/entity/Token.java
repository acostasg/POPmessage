package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "hash"
})
public class Token {

    @JsonProperty("hash")
    public String hash;

    public Token() {
    }

    public Token(String hash) {
        this.hash = hash;
    }

    public String hash() {
        return this.hash;
    }

    public boolean isEmpty() {
        return this.hash == null || this.hash.isEmpty();
    }

}
