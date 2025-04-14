package com.surpimi4.crud.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openroute", url = "${openrouteservice.api.url}")
public interface RouteClient {
    @GetMapping
    String getRoute(@RequestParam("start") String start,
                    @RequestParam("end")String end,
                    @RequestHeader("Authorization") String apiKey,
                    @RequestHeader("Accept") String acceptHeader);
}
