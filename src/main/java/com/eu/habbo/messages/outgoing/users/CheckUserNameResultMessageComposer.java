package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CheckUserNameResultMessageComposer extends MessageComposer {
    public static final int AVAILABLE = 0;
    public static final int TOO_SHORT = 2;
    public static final int TOO_LONG = 3;
    public static final int NOT_VALID = 4;
    public static final int TAKEN_WITH_SUGGESTIONS = 5;
    public static final int DISABLED = 6;

    private final int status;
    private final String name;
    private final List<String> suggestions;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.checkUserNameResultMessageComposer);
        this.response.appendInt(this.status);
        this.response.appendString(this.name);
        this.response.appendInt(this.suggestions.size());
        for (String suggestion : this.suggestions) {
            this.response.appendString(suggestion);
        }
        return this.response;
    }
}
