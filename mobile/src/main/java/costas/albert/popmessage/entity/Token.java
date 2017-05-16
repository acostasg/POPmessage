package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonPropertyOrder({
        "hash"
})
@JsonRootName(value = "Token")
public class Token {

    @JsonProperty("hash")
    private String hash;

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
