package com.eu.habbo.habbohotel.modtool;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

public class CfhCategory {
    @Getter
    private final String name;
    @Getter
    private final TIntObjectMap<CfhTopic> topics;

    public CfhCategory(String name) {
        this.name = name;
        this.topics = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    }

    public void addTopic(CfhTopic topic) {
        this.topics.put(topic.getId(), topic);
    }

}