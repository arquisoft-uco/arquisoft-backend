package com.arquisoft.config;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

@Configuration
public class FailedEventRetryConfig {

    private final FailedEventPublications failedEventPublications;
    private final GestorTraza gestorTraza;
    private final AppLogger logger;

    public FailedEventRetryConfig(FailedEventPublications failedEventPublications, GestorTraza gestorTraza,
                                  AppLogger logger) {
        this.failedEventPublications = failedEventPublications;
        this.gestorTraza = gestorTraza;
        this.logger = logger;
    }

    @Scheduled(fixedDelayString = "${arquisoft.events.failed-retry-interval:PT5M}")
    public void retryFailedEvents() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            logger.debug(MensajeriaKey.LOG_OUTBOX_REINTENTO_FALLIDOS, alcance.correlacionId());
            failedEventPublications.resubmit(
                    ResubmissionOptions.defaults().withMinAge(Duration.ofMinutes(2))
            );
        }
    }
}
