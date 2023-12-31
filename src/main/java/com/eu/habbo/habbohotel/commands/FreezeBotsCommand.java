package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class FreezeBotsCommand extends Command {
    public FreezeBotsCommand() {
        super("cmd_freeze_bots", Emulator.getTexts().getValue("commands.keys.cmd_freeze_bots").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return false;
        }

        if (gameClient.getHabbo().getHabboInfo().getId() == gameClient.getHabbo().getHabboInfo().getCurrentRoom().getOwnerId()
                || gameClient.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER)) {
            gameClient.getHabbo().getHabboInfo().getCurrentRoom().setAllowBotsWalk(!gameClient.getHabbo().getHabboInfo().getCurrentRoom().isAllowBotsWalk());
            gameClient.getHabbo().whisper(gameClient.getHabbo().getHabboInfo().getCurrentRoom().isAllowBotsWalk() ? getTextsValue("commands.succes.cmd_freeze_bots.unfrozen") : getTextsValue("commands.succes.cmd_freeze_bots.frozen"), RoomChatMessageBubbles.ALERT);
        } else {
            gameClient.getHabbo().whisper(getTextsValue("generic.cannot_do_that"), RoomChatMessageBubbles.ALERT);
        }

        return true;

    }
}
