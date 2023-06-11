package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoomUnitOnRollerComposer extends MessageComposer {
    // THIS IS WRONG SlideObjectBundleMessageComposer
    private final RoomUnit roomUnit;
    private final HabboItem roller;
    private final RoomTile oldLocation;
    private final double oldZ;
    private final RoomTile newLocation;
    private final double newZ;
    private final Room room;
    private final HabboItem oldTopItem;

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, HabboItem roller, RoomTile oldLocation, double oldZ, RoomTile newLocation, double newZ, Room room) {
        this.roomUnit = roomUnit;
        this.roller = roller;
        this.oldLocation = oldLocation;
        this.oldZ = oldZ;
        this.newLocation = newLocation;
        this.newZ = newZ;
        this.room = room;
        oldTopItem = this.room.getTopItemAt(oldLocation.getX(), oldLocation.getY());
    }

    public RoomUnitOnRollerComposer(RoomUnit roomUnit, RoomTile newLocation, Room room) {
        this.roomUnit = roomUnit;
        this.roller = null;
        this.oldLocation = this.roomUnit.getCurrentLocation();
        this.oldZ = this.roomUnit.getZ();
        this.newLocation = newLocation;
        this.newZ = this.newLocation.getStackHeight();
        this.room = room;
        this.oldTopItem = null;
    }

    @Override
    protected ServerMessage composeInternal() {
        if (!this.room.isLoaded())
            return null;

        this.response.init(Outgoing.slideObjectBundleMessageComposer);
        this.response.appendInt(this.oldLocation.getX());
        this.response.appendInt(this.oldLocation.getY());
        this.response.appendInt(this.newLocation.getX());
        this.response.appendInt(this.newLocation.getY());
        this.response.appendInt(0);
        this.response.appendInt(this.roller == null ? 0 : this.roller.getId());
        this.response.appendInt(2);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendString(this.oldZ + "");
        this.response.appendString(this.newZ + "");

        if (this.roller != null && room.getLayout() != null) {
            Emulator.getThreading().run(() -> {
                if(!this.roomUnit.isWalking() && this.roomUnit.getCurrentLocation() == this.oldLocation) {
                    HabboItem topItem = this.room.getTopItemAt(this.oldLocation.getX(), this.oldLocation.getY());
                    HabboItem topItemNewLocation = this.room.getTopItemAt(this.newLocation.getX(), this.newLocation.getY());

                    if (topItem != null && (oldTopItem == null || oldTopItem != topItemNewLocation)) {
                        try {
                            topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
                        } catch (Exception e) {
                            log.error("Caught exception", e);
                        }
                    }

                    this.roomUnit.setLocation(this.newLocation);
                    this.roomUnit.setZ(this.newLocation.getStackHeight());
                    this.roomUnit.setPreviousLocationZ(this.newLocation.getStackHeight());

                    if (topItemNewLocation != null && topItemNewLocation != roller && oldTopItem != topItemNewLocation) {
                        try {
                            topItemNewLocation.onWalkOn(this.roomUnit, this.room, new Object[]{this});
                        } catch (Exception e) {
                            log.error("Caught exception", e);
                        }
                    }
                }
            }, this.room.getRollerSpeed() == 0 ? 250 : InteractionRoller.DELAY);
            /*
            RoomTile rollerTile = room.getLayout().getTile(this.roller.getX(), this.roller.getY());
            Emulator.getThreading().run(() -> {
                if (this.oldLocation == rollerTile && this.roomUnit.getGoal() == rollerTile) {
                    this.roomUnit.setLocation(newLocation);
                    this.roomUnit.setGoalLocation(newLocation);
                    this.roomUnit.setPreviousLocationZ(newLocation.getStackHeight());
                    this.roomUnit.setZ(newLocation.getStackHeight());
                    this.roomUnit.sitUpdate = true;

                    HabboItem topItem = this.room.getTopItemAt(this.roomUnit.getCurrentLocation().x, this.roomUnit.getCurrentLocation().y);
                    if (topItem != null && topItem != roller && oldTopItem != topItem) {
                        try {
                            topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
                        } catch (Exception e) {
                            log.error("Caught exception", e);
                        }
                    }
                }
            }, this.room.getRollerSpeed() == 0 ? 250 : InteractionRoller.DELAY);
             */
        } else {
            this.roomUnit.setLocation(this.newLocation);
            this.roomUnit.setZ(this.newZ);
        }

        return this.response;
    }
}
