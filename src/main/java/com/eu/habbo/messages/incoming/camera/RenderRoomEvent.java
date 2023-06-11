package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.camera.CameraStorageUrlMessageComposer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RenderRoomEvent extends MessageHandler {
    @Override
    public void handle() {
        if (Emulator.getIntUnixTimestamp() - this.client.getHabbo().getHabboStats().getLastPurchaseTimestamp() < CatalogManager.PURCHASE_COOLDOWN) {
            return;
        }

        if (!this.client.getHabbo().hasPermission("acc_camera")) {
            this.client.getHabbo().alert(Emulator.getTexts().getValue("camera.permission"));
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null)
            return;
        
        final int count = this.packet.readInt();

        ByteBuf image = this.packet.getBuffer().readBytes(count);
        if (image == null)
            return;
        
        int timestamp = Emulator.getIntUnixTimestamp();

        String URL = this.client.getHabbo().getHabboInfo().getId() + "_" + timestamp + ".png";
        String URL_small = this.client.getHabbo().getHabboInfo().getId() + "_" + timestamp + "_small.png";
        String base = Emulator.getConfig().getValue("camera.url");
        String json = Emulator.getConfig().getValue("camera.extradata").replace("%timestamp%", timestamp + "").replace("%room_id%", room.getId() + "").replace("%url%", base + URL);
        this.client.getHabbo().getHabboInfo().setPhotoURL(base + URL);
        this.client.getHabbo().getHabboInfo().setPhotoTimestamp(timestamp);
        this.client.getHabbo().getHabboInfo().setPhotoRoomId(room.getId());
        this.client.getHabbo().getHabboInfo().setPhotoJSON(json);

        BufferedImage theImage = null;
        try {
            try (ByteBufInputStream in = new ByteBufInputStream(image)) {
                theImage = ImageIO.read(in);
            } finally {
                // from here we don't need the `image` buffer anymore
                image.clear();
                image.release();
            }
            File imageFile = new File(Emulator.getConfig().getValue("imager.location.output.camera") + URL);
            ImageIO.write(theImage, "png", imageFile);
            File smallImageFile = new File(Emulator.getConfig().getValue("imager.location.output.camera") + URL_small);
            ImageIO.write(theImage, "png", smallImageFile);
        } catch (IOException e) {
            e.printStackTrace();
            this.client.getHabbo().alert("Oops! Something went wrong :(");
            return;
        }

        this.client.sendResponse(new CameraStorageUrlMessageComposer(URL));
    }
}
