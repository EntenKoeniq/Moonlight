package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHanditem extends InteractionDefault {
    public InteractionHanditem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHanditem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (RoomLayout.tilesAdjecent(client.getHabbo().getRoomUnit().getCurrentLocation(), room.getLayout().getTile(this.getX(), this.getY())) ||
                (client.getHabbo().getRoomUnit().getCurrentLocation().getX() == this.getX() && client.getHabbo().getRoomUnit().getCurrentLocation().getY() == this.getY())) {
            this.handle(room, client.getHabbo().getRoomUnit());
        }
    }

    protected void handle(Room room, RoomUnit roomUnit) {
        if (this.getExtradata().isEmpty()) this.setExtradata("0");

        if (!this.getExtradata().equals("0")) return;

        HabboItem instance = this;
        room.giveHandItem(roomUnit, this.getBaseItem().getRandomVendingItem());

        if (this.getBaseItem().getStateCount() > 1) {
            this.setExtradata("1");
            room.updateItem(this);

            Emulator.getThreading().run(() -> {
                InteractionHanditem.this.setExtradata("0");
                room.updateItem(instance);
            }, 500);
        }
    }
}