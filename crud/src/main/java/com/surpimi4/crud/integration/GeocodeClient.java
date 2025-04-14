package com.surpimi4.crud.integration;

import com.surpimi4.crud.dto.GeocodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "geocode", url = "${dadata.api-url}")
public interface GeocodeClient {
    @PostMapping
    GeocodeResponse[] geocode(
            @RequestBody List<String> addresses,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Secret") String secret
    );
}
