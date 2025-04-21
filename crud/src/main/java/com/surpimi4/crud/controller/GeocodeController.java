package com.surpimi4.crud.controller;


import com.surpimi4.crud.annotation.Internal;
import com.surpimi4.crud.dto.*;
import com.surpimi4.crud.repository.UserInfoRepository;
import com.surpimi4.crud.service.GeocodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;


@RestController
@RequestMapping("/geocode/api")
public class GeocodeController {

    private final GeocodeService geocodeService;
    private final UserInfoRepository userInfoRepository;

    public GeocodeController(GeocodeService geocodeService, UserInfoRepository userInfoRepository) {
        this.geocodeService = geocodeService;
        this.userInfoRepository = userInfoRepository;
    }

    @Internal
    @PostMapping("/home")
    public ResponseEntity<GeocodeResponse> getHomeAddress(@RequestBody TelegramAddressRequest request) {
        Long chatId = request.getChatId();
        String address = request.getAddress();
        ResponseEntity<GeocodeResponse> response = geocodeService.saveHomeAddressInfoAndTimezone(chatId, address);
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.status(400).build();
        }
        return response;
    }

    @Internal
    @PostMapping("/work")
    public ResponseEntity<GeocodeResponse> getWorkAddress(@RequestBody TelegramAddressRequest request) {
        Long chatId = request.getChatId();
        String address = request.getAddress();
        ResponseEntity<GeocodeResponse> response = geocodeService.saveWorkAddressInfo(chatId, address);
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.status(400).build();
        }
        return response;
    }

    @Internal
    @PostMapping("/userInfo")
    public ResponseEntity<UserInfoDTO> getUserInfo(@RequestBody TelegramChatIdRequest request) {
        Long chatId = request.getChatId();

        return userInfoRepository.findById(chatId)
                .map(userInfo -> new UserInfoDTO(
                        userInfo.getId(),
                        userInfo.getHomeAddress(),
                        userInfo.getHomeLatitude(),
                        userInfo.getHomeLongitude(),
                        userInfo.getWorkAddress(),
                        userInfo.getWorkLatitude(),
                        userInfo.getWorkLongitude(),
                        userInfo.getTimezone(),
                        userInfo.getTime()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());


    }

    @Internal
    @PostMapping("/time")
    public ResponseEntity<Void> getUserTime(@RequestBody UserTimeRequest request) {
        Long chatId = request.getChatId();
        LocalTime userTime = request.getTime();
        ResponseEntity<Void> response = geocodeService.saveUserTime(chatId, userTime);
        if (response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.ok().build();
    }

    @Internal
    @DeleteMapping("/userInfo")
    public ResponseEntity<Void> deleteUserInfo(@RequestBody TelegramChatIdRequest request) {
        Long chatId = request.getChatId();
        geocodeService.deleteUserInfo(chatId);
        if (userInfoRepository.findById(chatId).isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }


}
