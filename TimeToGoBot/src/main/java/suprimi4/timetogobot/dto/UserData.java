package suprimi4.timetogobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private String chatId;
    private String homeAddress;
    private String workAddress;
    private LocalTime arriveTime;
    private LocalDate lastNotificationDate;
    private Double durationTime;

}
