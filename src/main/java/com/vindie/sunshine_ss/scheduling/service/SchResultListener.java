package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_scheduler_dto.SchProgress;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@EnableRabbit
@AllArgsConstructor
public class SchResultListener {

    private SchService schService;

    @RabbitListener(queues = "schProgressQueue")
    public void listener(SchProgress schProgress) {
        switch (schProgress.getStatus()) {
            case IN_PROGRESS -> Optional.ofNullable(schService.getStartedSchedules().get(schProgress.getUuid()))
                    .ifPresent(scheduleInfo -> scheduleInfo.setLastUpdate(LocalDateTime.now()));
            case DONE -> schService.saveSchResult(schProgress.getUuid());
            case ERROR -> schService.processErrorSchResult(schProgress.getUuid());
        }
    }

}
