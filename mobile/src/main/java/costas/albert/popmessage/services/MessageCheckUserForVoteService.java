package costas.albert.popmessage.services;

import java.util.List;

import costas.albert.popmessage.entity.User;
import costas.albert.popmessage.entity.Vote;

public final class MessageCheckUserForVoteService {
    private List<Vote> list;

    private MessageCheckUserForVoteService(List<Vote> list) {
        this.list = list;
    }

    public static boolean run(List<Vote> list, User user) {
        MessageCheckUserForVoteService messageFilter = new MessageCheckUserForVoteService(list);
        return messageFilter.apply(user);
    }

    private boolean apply(User user) {
        for (Vote vote : list) {
            if (null != vote.UserID() && vote.UserID().equals(user.Id())) {
                return true;
            }
        }
        return false;
    }
}