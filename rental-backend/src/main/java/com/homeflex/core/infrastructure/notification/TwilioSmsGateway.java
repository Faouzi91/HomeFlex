package com.homeflex.core.infrastructure.notification;

import com.homeflex.core.config.AppProperties;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwilioSmsGateway {

    private final AppProperties appProperties;

    @PostConstruct
    public void init() {
        var twilio = appProperties.getTwilio();
        if (twilio.isEnabled() && twilio.getAccountSid() != null) {
            Twilio.init(twilio.getAccountSid(), twilio.getAuthToken());
            log.info("Twilio SMS Gateway initialized (from={})", twilio.getFromNumber());
        } else {
            log.warn("Twilio SMS not configured or disabled — skipping initialization");
        }
    }

    public void sendSms(String to, String body) {
        var twilio = appProperties.getTwilio();
        if (!twilio.isEnabled() || twilio.getAccountSid() == null) {
            log.debug("Twilio disabled. Would send SMS to {}: {}", to, body);
            return;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilio.getFromNumber()),
                    body
            ).create();
            log.info("SMS sent to {}: sid={}", to, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", to, e);
        }
    }

    public void sendWhatsApp(String to, String body) {
        var twilio = appProperties.getTwilio();
        if (!twilio.isEnabled() || twilio.getAccountSid() == null) {
            log.debug("Twilio disabled. Would send WhatsApp to {}: {}", to, body);
            return;
        }

        try {
            // Twilio WhatsApp requires 'whatsapp:' prefix
            String toPrefix = to.startsWith("whatsapp:") ? to : "whatsapp:" + to;
            String fromPrefix = twilio.getFromWhatsApp().startsWith("whatsapp:") 
                ? twilio.getFromWhatsApp() 
                : "whatsapp:" + twilio.getFromWhatsApp();

            Message message = Message.creator(
                    new PhoneNumber(toPrefix),
                    new PhoneNumber(fromPrefix),
                    body
            ).create();
            log.info("WhatsApp sent to {}: sid={}", to, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send WhatsApp to {}", to, e);
        }
    }
}
