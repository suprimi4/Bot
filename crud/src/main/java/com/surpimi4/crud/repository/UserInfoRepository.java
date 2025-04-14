package com.surpimi4.crud.repository;

import com.surpimi4.crud.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

}
