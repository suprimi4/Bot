package suprimi4.timetogobot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaConfig {
    public static final String BOT_COMMAND_TOPIC = "bot-command-topic";
    public static final String BOT_COMMAND_CONFIRM_TOPIC = "bot-command-confirm-topic";
    public static final String ALERT_TOPIC = "alert-topic";

    @Bean
    NewTopic createBotCommandTopic() {
        return TopicBuilder
                .name(BOT_COMMAND_TOPIC)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();

    }


    @Bean
    NewTopic createBotConfirmTopic() {
        return TopicBuilder
                .name(BOT_COMMAND_CONFIRM_TOPIC)
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();

    }


    @Bean
    NewTopic createAlertTopic() {
        return TopicBuilder
                .name(ALERT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }


}
