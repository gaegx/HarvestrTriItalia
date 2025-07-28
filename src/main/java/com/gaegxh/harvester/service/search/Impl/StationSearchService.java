package com.gaegxh.harvester.service.search.Impl;

import com.gaegxh.harvester.model.Station;
import com.google.gson.Gson;

import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

@Service
public class StationSearchService {

    private static final String SEARCH_URL = "https://www.lefrecce.it/Channels.Website.BFF.WEB/website/locations/search";
    private final Gson gson = new Gson();

    public Station[] searchStations(String name, int limit) {
        String json = Unirest.get(SEARCH_URL)
                .queryString("name", name)
                .queryString("limit", limit)
                .header("Accept", "application/json")
                .asString()
                .getBody();

        return gson.fromJson(json, Station[].class);
    }
}
