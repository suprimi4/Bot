package com.surpimi4.crud.service;


import com.surpimi4.crud.dto.GeocodeResponse;
import com.surpimi4.crud.integration.GeocodeClient;
import com.surpimi4.crud.model.UserInfo;
import com.surpimi4.crud.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;


import java.time.LocalTime;
import java.util.Collections;

import java.util.Optional;


@Service
public class GeocodeService {

    @Value("${dadata.client.token}")
    private String token;

    @Value("${dadata.client.secret}")
    private String secret;

    private final UserInfoRepository userInfoRepository;
    private final GeocodeClient geocodeClient;

    public GeocodeService(UserInfoRepository userInfoRepository, GeocodeClient geocodeClient) {
        this.userInfoRepository = userInfoRepository;
        this.geocodeClient = geocodeClient;
    }

    public ResponseEntity<GeocodeResponse> saveHomeAddressInfoAndTimezone(Long chatId, String address) {

        ResponseEntity<GeocodeResponse> response = getGeocode(address);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        GeocodeResponse info = response.getBody();

        UserInfo userInfo = new UserInfo();
        userInfo.setId(chatId);
        userInfo.setHomeAddress(info.getResult());
        userInfo.setHomeLatitude(info.getLatitude());
        userInfo.setHomeLongitude(info.getLongitude());
        userInfo.setTimezone(info.getTimezone());
        userInfoRepository.save(userInfo);
        return response;

    }


    public ResponseEntity<GeocodeResponse> saveWorkAddressInfo(Long chatId, String address) {

        ResponseEntity<GeocodeResponse> response = getGeocode(address);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(chatId);
        if (optionalUserInfo.isPresent()) {
            GeocodeResponse info = response.getBody();
            UserInfo userInfo = optionalUserInfo.get();
            userInfo.setWorkAddress(info.getResult());
            userInfo.setWorkLatitude(info.getLatitude());
            userInfo.setWorkLongitude(info.getLongitude());
            userInfoRepository.save(userInfo);
        }


        return response;

    }


    public ResponseEntity<Void> saveUserTime(Long chatId, LocalTime localTime) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(chatId);
        if (optionalUserInfo.isPresent()) {
            UserInfo currentUserInfo = optionalUserInfo.get();
            currentUserInfo.setTime(localTime);
            userInfoRepository.save(currentUserInfo);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<GeocodeResponse> getGeocode(String address) {
        GeocodeResponse[] response = geocodeClient.geocode(
                Collections.singletonList(address),
                "Token " + token,
                secret
        );

        if (response == null || response.length == 0 || response[0].getResult() == null) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.ok(response[0]);
    }

}
