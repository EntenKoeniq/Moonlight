package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.RoomForwardMessageComposer;
import com.google.gson.Gson;

public class ForwardUser extends RCONMessage<ForwardUser.ForwardUserJSON> {
    public ForwardUser() {
        super(ForwardUserJSON.class);
    }

    @Override
    public void handle(Gson gson, ForwardUserJSON object) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);
        if (habbo == null) {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }

        Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(object.room_id);
        if (room == null) {
            this.status = RCONMessage.ROOM_NOT_FOUND;
            return;
        }
        
        if (habbo.getHabboInfo().getCurrentRoom() != null) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, habbo.getHabboInfo().getCurrentRoom());
        }

        habbo.getClient().sendResponse(new RoomForwardMessageComposer(object.room_id));
        Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, object.room_id, "", true);

        this.status = RCONMessage.STATUS_OK;
    }

    static class ForwardUserJSON {
        public int user_id;
        public int room_id;
    }
}
