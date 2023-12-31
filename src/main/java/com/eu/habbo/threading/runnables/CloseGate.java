package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloseGate implements Runnable {
    private final HabboItem gate;
    private final Room room;

    @Override
    public void run() {
        if (this.gate.getRoomId() == this.room.getId()) {
            if (this.room.isLoaded()) {
                if (this.room.getHabbosAt(this.gate.getX(), this.gate.getY()).isEmpty()) {
                    this.gate.setExtradata("0");
                    this.room.updateItem(this.gate);
                    this.gate.needsUpdate(true);
                }
            }
        }
    }
}
