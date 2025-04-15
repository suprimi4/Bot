package suprimi4.timetogobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String homeAddress;
    private Double homeLatitude;
    private Double homeLongitude;
    private String workAddress;
    private Double workLatitude;
    private Double workLongitude;
    private String timezone;
    private LocalTime time;


}

