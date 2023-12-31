package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.TraxSongInfoMessageComposer;

import java.util.ArrayList;
import java.util.List;

public class GetSongInfoEvent extends MessageHandler {
    @Override
    public void handle() {
        int count = this.packet.readInt();

        List<SoundTrack> tracks = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(this.packet.readInt());

            if (track != null)
                tracks.add(track);
        }

        this.client.sendResponse(new TraxSongInfoMessageComposer(tracks));
    }
}
