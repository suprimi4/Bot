package com.surpimi4.crud.service;

import com.surpimi4.crud.repository.UserInfoRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class ActiveUserService {
    private final UserInfoRepository userInfoRepository;

    public ActiveUserService(MeterRegistry registry, UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;


        Gauge.builder("telegram.users.count", userInfoRepository, CrudRepository::count)
                .tag("table", "user_info")
                .description("Текущее количество записей в таблице пользователей")
                .register(registry);
    }
}
