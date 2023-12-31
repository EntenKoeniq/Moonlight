package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Default_3x3Layout extends CatalogPage {

    public Default_3x3Layout(ResultSet set) throws SQLException {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendString("default_3x3");
        appendImagesAndText(message);
    }
}
