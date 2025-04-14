package com.surpimi4.crud.controller;


import com.surpimi4.crud.dto.TelegramAddressRequest;
import com.surpimi4.crud.dto.GeocodeResponse;
import com.surpimi4.crud.dto.TelegramChatIdRequest;
import com.surpimi4.crud.dto.UserInfoDTO;
import com.surpimi4.crud.repository.UserInfoRepository;
import com.surpimi4.crud.service.GeocodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/geocode/api")
public class GeocodeController {

    private final GeocodeService geocodeService;
    private final UserInfoRepository userInfoRepository;

    public GeocodeController(GeocodeService geocodeService, UserInfoRepository userInfoRepository) {
        this.geocodeService = geocodeService;
        this.userInfoRepository = userInfoRepository;
    }

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
                        userInfo.getTimezone()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());




    }




}
