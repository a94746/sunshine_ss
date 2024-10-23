package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.common.config.KafkaConsumer;
import com.vindie.sunshine_ss.scheduling.dto.SchProgress;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SchResultListener {

    private SchService schService;

    @KafkaListener(topics = KafkaConsumer.SCH_PROGRESS_TOPIC_NAME, groupId = "group1")
    void listener(SchProgress schProgress) {
        switch (schProgress.getStatus()) {
            case IN_PROGRESS -> Optional.ofNullable(schService.getStartedSchedules().get(schProgress.getUuid()))
                    .ifPresent(scheduleInfo -> scheduleInfo.setLastUpdate(LocalDateTime.now()));
            case DONE -> schService.saveSchResult(schProgress.getUuid());
            case ERROR -> schService.processErrorSchResult(schProgress.getUuid());
        }
    }
}
