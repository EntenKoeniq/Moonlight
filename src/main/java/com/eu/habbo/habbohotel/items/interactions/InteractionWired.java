package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.OneWayDoorStatusMessageComposer;
import gnu.trove.map.hash.TLongLongHashMap;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public abstract class InteractionWired extends InteractionDefault {
    private long cooldown;
    private final TLongLongHashMap userExecutionCache = new TLongLongHashMap(3);

    InteractionWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    InteractionWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    public abstract boolean execute(RoomUnit roomUnit, Room room, Object[] stuff);

    public abstract String getWiredData();

    public abstract void serializeWiredData(ServerMessage message, Room room);

    public abstract void loadWiredData(ResultSet set, Room room) throws SQLException;

    @Override
    public void run() {
        if (this.needsUpdate()) {
            String wiredData = this.getWiredData();

            if (wiredData == null) {
                wiredData = "";
            }

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE items SET wired_data = ? WHERE id = ?")) {
                if (this.getRoomId() != 0) {
                    statement.setString(1, wiredData);
                } else {
                    statement.setString(1, "");
                }
                statement.setInt(2, this.getId());
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }
        }
        super.run();
    }

    @Override
    public void onPickUp(Room room) {
        this.onPickUp();
    }

    public abstract void onPickUp();

    public void activateBox(Room room) {
        this.activateBox(room, null, 0L);
    }

    public void activateBox(Room room, RoomUnit roomUnit, long millis) {
        this.setExtradata(this.getExtradata().equals("1") ? "0" : "1");
        room.sendComposer(new OneWayDoorStatusMessageComposer(this).compose());
        if (roomUnit != null) {
            this.addUserExecutionCache(roomUnit.getId(), millis);
        }
    }

    protected long requiredCooldown() {
        return 50L;
    }


    public boolean canExecute(long newMillis) {
        return newMillis - this.cooldown >= this.requiredCooldown();
    }

    public void setCooldown(long newMillis) {
        this.cooldown = newMillis;
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    public boolean userCanExecute(int roomUnitId, long timestamp) {
        if (roomUnitId != -1) {
            if (this.userExecutionCache.containsKey(roomUnitId)) {
                long lastTimestamp = this.userExecutionCache.get(roomUnitId);
                return timestamp - lastTimestamp >= 100L;
            }

        }
        return true;
    }

    public void clearUserExecutionCache() {
        this.userExecutionCache.clear();
    }

    public void addUserExecutionCache(int roomUnitId, long timestamp) {
        this.userExecutionCache.put(roomUnitId, timestamp);
    }

    public static WiredSettings readSettings(ClientMessage packet, boolean isEffect)
    {
        int intParamCount = packet.readInt();
        int[] intParams = new int[intParamCount];

        for(int i = 0; i < intParamCount; i++)
        {
            intParams[i] = packet.readInt();
        }

        String stringParam = packet.readString();

        int itemCount = packet.readInt();
        int[] itemIds = new int[itemCount];

        for(int i = 0; i < itemCount; i++)
        {
            itemIds[i] = packet.readInt();
        }

        WiredSettings settings = new WiredSettings(intParams, stringParam, itemIds, -1);

        if(isEffect)
        {
            settings.setDelay(packet.readInt());
        }

        settings.setStuffTypeSelectionCode(packet.readInt());
        return settings;
    }

}