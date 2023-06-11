package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.google.gson.Gson;

public class SetRank extends RCONMessage<SetRank.JSONSetRank> {
    public SetRank() {
        super(JSONSetRank.class);
    }

    @Override
    public void handle(Gson gson, JSONSetRank object) {
        try {
            Emulator.getGameEnvironment().getHabboManager().setRank(object.user_id, object.rank);
        } catch (Exception e) {
            this.status = RCONMessage.SYSTEM_ERROR;
            this.message = "invalid rank";
            return;
        }

        this.message = (Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id) != null) ? "updated online user" : "updated offline user";
    }

    static class JSONSetRank {
        public int user_id;
        public int rank;
    }
}
