package com.vindie.sunshine_ss.common.email;

import com.vindie.sunshine_ss.common.service.PropertiesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EmailSenderService {

    private JavaMailSender mailSender;
    private final PropertiesService properties;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.fromEmailAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        if (properties.isTestMode) {
            log.info("Sended email = {}", message);
        } else {
            mailSender.send(message);
        }
    }

}
