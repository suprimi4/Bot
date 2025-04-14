package suprimi4.timetogobot.api;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import suprimi4.timetogobot.config.FeignClientConfig;
import suprimi4.timetogobot.dto.GeocodeResponse;
import suprimi4.timetogobot.dto.TelegramAddressRequest;
import suprimi4.timetogobot.dto.TelegramChatIdRequest;
import suprimi4.timetogobot.dto.UserInfoDTO;

@FeignClient(name = "geocode-api", url = "${api.url}", configuration = FeignClientConfig.class)
public interface GeocodeApiClient {

    @PostMapping("/geocode/api/home")
    GeocodeResponse resolveHomeAddress(TelegramAddressRequest request);

    @PostMapping("/geocode/api/work")
    GeocodeResponse resolveWorkAddress(TelegramAddressRequest request);

    @PostMapping("/geocode/api/userInfo")
    UserInfoDTO getUserInfo(TelegramChatIdRequest request);

}
