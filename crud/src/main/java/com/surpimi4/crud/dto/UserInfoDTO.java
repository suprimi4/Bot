package com.surpimi4.crud.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {

    private Long id;

    private String homeAddress;

    private Double homeLatitude;

    private Double homeLongitude;

    private String workAddress;

    private Double workLatitude;

    private Double workLongitude;

    private String timezone;



}
