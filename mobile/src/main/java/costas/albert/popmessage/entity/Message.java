package costas.albert.popmessage.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import costas.albert.popmessage.services.MessageCheckUserForVoteService;
import costas.albert.popmessage.services.MessageFilterService;
import costas.albert.popmessage.wrapper.EncodeMessageWrapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ID",
        "text",
        "location",
        "votes",
        "status"
})
@JsonRootName(value = "Message")
public class Message {

    @JsonProperty("Id")
    private Id ID;
    @JsonProperty("user")
    private User user;
    @JsonProperty("text")
    private String text;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("votes")
    private List<Vote> votes;
    @JsonProperty("status")
    private Status status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Id")
    Id getID() {
        return ID;
    }

    @JsonProperty("Id")
    public void setID(Id ID) {
        this.ID = ID;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    public String userId() {
        return this.getUser().getID().getId();
    }

    public String userName() {
        return this.getUser().getName();
    }

    @JsonIgnore
    public String getText() {
        return EncodeMessageWrapper.Decoder(text);
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
    public List<Vote> getVotes() {
        return votes;
    }

    @JsonProperty("votes")
    public void setVotes(List<Vote> votes) {
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

    public int getSummaryVotesLike() {
        return MessageFilterService.run(votes, Type.POSITIVE).size();
    }

    public int getSummaryVotesDislike() {
        return MessageFilterService.run(votes, Type.NEGATIVE).size();
    }

    public boolean isVotedFromUser(User user) {
        return MessageCheckUserForVoteService.run(votes, user);
    }

    @JsonIgnore
    public String Id() {
        return this.getID().getId();
    }

}
