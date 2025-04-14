package com.surpimi4.crud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surpimi4.crud.integration.RouteClient;
import com.surpimi4.crud.model.UserInfo;
import com.surpimi4.crud.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RouteService {
    private final UserInfoRepository userInfoRepository;
    private final RouteClient routeClient;

    @Value("${openrouteservice.api.key}")
    private String openRouteServiceApiKey;

    public RouteService(UserInfoRepository userInfoRepository, RouteClient routeClient) {
        this.userInfoRepository = userInfoRepository;
        this.routeClient = routeClient;
    }

    public Double getRouteByChatId(Long chatId) {
        UserInfo userInfo = userInfoRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("User with chatId " + chatId + " not found"));

        Double homeLon = userInfo.getHomeLongitude();
        Double homeLat = userInfo.getHomeLatitude();
        Double workLon = userInfo.getWorkLongitude();
        Double workLat = userInfo.getWorkLatitude();

        String startCoordinates = homeLon + "," + homeLat;
        String finishCoordinates = workLon + "," + workLat;

        String response = routeClient.getRoute(
                startCoordinates,
                finishCoordinates,
                openRouteServiceApiKey,
                "application/geo+json;charset=UTF-8"
        );

        return getDurationFromResponse(response);

    }

    private Double getDurationFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            return root
                    .path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("duration")
                    .asDouble();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
