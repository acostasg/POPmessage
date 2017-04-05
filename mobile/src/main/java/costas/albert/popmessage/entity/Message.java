package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ID",
        "userID",
        "text",
        "location",
        "votes",
        "status"
})
public class Message {

    @JsonProperty("ID")
    private Id ID;
    @JsonProperty("userID")
    private Id userID;
    @JsonProperty("text")
    private String text;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("votes")
    private Vote votes;
    @JsonProperty("status")
    private Status status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ID")
    public Id getID() {
        return ID;
    }

    @JsonProperty("ID")
    public void setID(Id ID) {
        this.ID = ID;
    }

    @JsonProperty("userID")
    public Id getUserID() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserID(Id userID) {
        this.userID = userID;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("votes")
    public Vote getVotes() {
        return votes;
    }

    @JsonProperty("votes")
    public void setVotes(Vote votes) {
        this.votes = votes;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
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
