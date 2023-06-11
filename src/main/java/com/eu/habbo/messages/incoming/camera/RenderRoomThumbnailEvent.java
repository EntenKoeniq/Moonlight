package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.camera.ThumbnailStatusMessageComposer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RenderRoomThumbnailEvent extends MessageHandler {
    @Override
    public void handle() {
        if (!this.client.getHabbo().hasPermission("acc_camera")) {
            this.client.getHabbo().alert(Emulator.getTexts().getValue("camera.permission"));
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null)
            return;

        if (!room.isOwner(this.client.getHabbo()))
            return;

        final int count = this.packet.readInt();

        ByteBuf image = this.packet.getBuffer().readBytes(count);
        if(image == null)
            return;
        
        BufferedImage theImage = null;
        try {
            try (ByteBufInputStream in = new ByteBufInputStream(image)) {
                theImage = ImageIO.read(in);
            } finally {
                // from here we don't need the `image` buffer anymore
                image.clear();
                image.release();
            }
            File imageFile = new File(Emulator.getConfig().getValue("imager.location.output.thumbnail") + room.getId() + ".png");
            ImageIO.write(theImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            this.client.getHabbo().alert("Oops! Something went wrong :(");
            return;
        }
        this.client.sendResponse(new ThumbnailStatusMessageComposer());
    }
}