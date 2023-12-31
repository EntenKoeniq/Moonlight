package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveItemFromTradeEvent extends MessageHandler {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        RoomTrade trade = this.client.getHabbo().getHabboInfo().getCurrentRoom().getActiveTradeForHabbo(this.client.getHabbo());
        if (trade != null) {
            HabboItem item = trade.getRoomTradeUserForHabbo(this.client.getHabbo()).getItem(itemId);

            if (!trade.getRoomTradeUserForHabbo(this.client.getHabbo()).isAccepted() && item != null) {
                trade.removeItem(this.client.getHabbo(), item);
            }
        }
    }
}
