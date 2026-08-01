package com.arquisoft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

@Slf4j
@Configuration
public class FailedEventRetryConfig {

    private final FailedEventPublications failedEventPublications;

    public FailedEventRetryConfig(FailedEventPublications failedEventPublications) {
        this.failedEventPublications = failedEventPublications;
    }

    @Scheduled(fixedDelayString = "${arquisoft.events.failed-retry-interval:PT5M}")
    public void retryFailedEvents() {
        log.debug("Verificando eventos de dominio con estado FAILED para reintento...");
        failedEventPublications.resubmit(
                ResubmissionOptions.defaults().withMinAge(Duration.ofMinutes(2))
        );
    }
}
