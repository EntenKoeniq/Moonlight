package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.outgoing.users.BadgeReceivedComposer;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class GiveBadge extends RCONMessage<GiveBadge.GiveBadgeJSON> {
    public GiveBadge() {
        super(GiveBadgeJSON.class);
    }

    @Override
    public void handle(Gson gson, GiveBadgeJSON json) {
        if (json.badge.isEmpty()) {
            return;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);

        String username = Integer.toString(json.user_id);
        if (habbo != null) {
            username = habbo.getHabboInfo().getUsername();

            for (String badgeCode : json.badge.split(";")) {
                if (habbo.getInventory().getBadgesComponent().hasBadge(badgeCode)) {
                    continue;
                }

                HabboBadge badge = new HabboBadge(0, badgeCode, 0, habbo);
                badge.run();

                habbo.getInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new BadgeReceivedComposer(badge));

                this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", badgeCode);
            }

            return;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            for (String badgeCode : json.badge.split(";")) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(slot_id) FROM users_badges INNER JOIN users ON users.id = user_id WHERE users.id = ? AND badge_code = ? LIMIT 1")) {
                    statement.setInt(1, json.user_id);
                    statement.setString(2, badgeCode);
                    try (ResultSet set = statement.executeQuery()) {
                        if (set.next()) {
                            if (set.getInt(1) != 0) {
                                continue;
                            }
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_badges VALUES (null, (SELECT id FROM users WHERE users.id = ? LIMIT 1), 0, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, json.user_id);
                    statement.setString(2, badgeCode);
                    statement.execute();
                }

                this.message = Emulator.getTexts().getValue("commands.succes.cmd_badge.given").replace("%user%", username).replace("%badge%", badgeCode);
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
            this.status = RCONMessage.STATUS_ERROR;
            this.message = e.getMessage();
        }
    }

    static class GiveBadgeJSON {
        public int user_id;
        public String badge;
    }
}
