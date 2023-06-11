package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class HabboSearchResultComposer extends MessageComposer {
    private final THashSet<MessengerBuddy> users;
    private final THashSet<MessengerBuddy> friends;

    private static final Comparator<MessengerBuddy> COMPARATOR = Comparator.comparing((MessengerBuddy b) -> b.getUsername().length()).thenComparing((MessengerBuddy b, MessengerBuddy b2) -> b.getUsername().compareToIgnoreCase(b2.getUsername()));

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.habboSearchResultComposer);
        List<MessengerBuddy> u = new ArrayList<>();

        for (MessengerBuddy buddy : this.users) {
            if (!this.inFriendList(buddy)) {
                u.add(buddy);
            }
        }

        List<MessengerBuddy> friends = new ArrayList<>(this.friends);

        u.sort(HabboSearchResultComposer.COMPARATOR);
        friends.sort(HabboSearchResultComposer.COMPARATOR);

        this.response.appendInt(this.friends.size());
        for (MessengerBuddy buddy : this.friends) {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getLook());
            this.response.appendString("");
        }

        this.response.appendInt(u.size());
        for (MessengerBuddy buddy : u) {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getOnline() == 1 ? buddy.getLook() : "");
            this.response.appendString("");
        }

        return this.response;
    }

    private boolean inFriendList(MessengerBuddy buddy) {
        for (MessengerBuddy friend : this.friends) {
            if (friend.getUsername().equals(buddy.getUsername()))
                return true;
        }

        return false;
    }
}
