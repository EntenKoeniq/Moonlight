package com.eu.habbo.messages.outgoing.users.verification;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TryPhoneNumberResultMessageComposer extends MessageComposer {
    private final int unknownInt1;
    private final int unknownInt2;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.tryPhoneNumberResultMessageComposer);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.unknownInt2);
        return this.response;
    }
}