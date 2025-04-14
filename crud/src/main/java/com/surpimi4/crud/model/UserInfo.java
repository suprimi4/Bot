package com.surpimi4.crud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "address")
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {
    @Id
    private Long id;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "home_latitude")
    private Double homeLatitude;

    @Column(name = "home_longitude")
    private Double homeLongitude;

    @Column(name = "work_address")
    private String workAddress;

    @Column(name = "work_latitude")
    private Double workLatitude;

    @Column(name = "work_longitude")
    private Double workLongitude;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "time")
    private LocalTime time;


}
