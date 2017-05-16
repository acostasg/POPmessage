package costas.albert.popmessage.services;

import java.util.ArrayList;
import java.util.List;

import costas.albert.popmessage.entity.Type;
import costas.albert.popmessage.entity.Vote;

public final class MessageFilterService {
    private List<Vote> list;

    public MessageFilterService(List<Vote> list) {
        this.list = list;
    }

    public static List<Vote> run(List<Vote> list, Type type) {
        MessageFilterService messageFilter = new MessageFilterService(list);
        return messageFilter.apply(type);
    }

    public List<Vote> apply(Type type) {
        List<Vote> filter = new ArrayList<>();

        for (Vote vote : list) {
            if (vote.getType() == type) {
                filter.add(vote);
            }
        }
        return filter;
    }
}