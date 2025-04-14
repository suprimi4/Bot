package suprimi4.timetogobot.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import suprimi4.timetogobot.config.FeignClientConfig;
import suprimi4.timetogobot.dto.TelegramChatIdRequest;

@FeignClient(name = "route-api", url = "${api.url}", configuration = FeignClientConfig.class)
public interface RouteApiClient {

    @PostMapping("/routing/api/duration")
    double getRouteDuration(TelegramChatIdRequest telegramChatIdRequest);

}
