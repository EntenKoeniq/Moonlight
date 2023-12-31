package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.GuideReportingStatusMessageComposer;

import java.util.Calendar;

public class GetGuideReportingStatusEvent extends MessageHandler {
    @Override
    public void handle() {
        GuardianTicket ticket = Emulator.getGameEnvironment().getGuideManager().getRecentTicket(this.client.getHabbo());

        if (ticket != null) {
            if (ticket.inProgress()) {
                this.client.sendResponse(new GuideReportingStatusMessageComposer(GuideReportingStatusMessageComposer.ONGOING_HELPER_CASE, 1));
                return;
            }

            if ((Calendar.getInstance().getTime().getTime() / 1000) - ticket.getDate().getTime() < Emulator.getConfig().getInt("guardians.reporting.cooldown")) {
                this.client.sendResponse(new GuideReportingStatusMessageComposer(GuideReportingStatusMessageComposer.TOO_RECENT, 1));
                return;
            }
        }

        this.client.sendResponse(new GuideReportingStatusMessageComposer(GuideReportingStatusMessageComposer.START_REPORT, 0));
    }
}
