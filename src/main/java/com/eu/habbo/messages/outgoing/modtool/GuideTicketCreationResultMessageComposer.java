package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GuideTicketCreationResultMessageComposer extends MessageComposer {
    public static final int RECEIVED = 0;
    public static final int IGNORED = 1;
    public static final int NO_CHAT = 2;
    public static final int ALREADY_REPORTED = 3;
    public static final int NO_MISSUSE = 4;

    private final int code;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.guideTicketCreationResultMessageComposer);
        this.response.appendInt(this.code);
        return this.response;
    }
}
