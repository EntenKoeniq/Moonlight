package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModeratorRoomInfoComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.moderatorRoomInfoComposer);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.room.getCurrentHabbos().size());
        this.response.appendBoolean(this.room.getHabbo(this.room.getOwnerId()) != null);
        this.response.appendInt(this.room.getOwnerId());
        this.response.appendString(this.room.getOwnerName());
        this.response.appendBoolean(true);
        this.response.appendString(this.room.getName());
        this.response.appendString(this.room.getDescription());
        this.response.appendInt(this.room.getTags().split(";").length);
        for (int i = 0; i < this.room.getTags().split(";").length; i++) {
            this.response.appendString(this.room.getTags().split(";")[i]);
        }
        return this.response;
    }
}
