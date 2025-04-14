package com.surpimi4.crud.controller;

import com.surpimi4.crud.dto.TelegramChatIdRequest;
import com.surpimi4.crud.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing/api")
public class RouteController {

    private final RouteService routeService;


    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }
    @PostMapping("/duration")
    public ResponseEntity<Double> getRouteDuration(@RequestBody TelegramChatIdRequest request) {
        double duration = routeService.getRouteByChatId(request.getChatId());
        return ResponseEntity.ok(duration);
    }

}
