package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "messageID",
        "userID",
        "type",
})

@JsonRootName(value = "Vote")
public class Vote {

    @JsonProperty("messageID")
    private Id messageID;
    @JsonProperty("userID")
    private Id userID;
    @JsonProperty("type")
    private Type type;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("messageID")
    public Id getMessageID() {
        return messageID;
    }

    @JsonProperty("messageID")
    public void setMessageID(Id messageID) {
        this.messageID = messageID;
    }

    @JsonProperty("userID")
    public Id getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(Id userID) {
        this.userID = userID;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}