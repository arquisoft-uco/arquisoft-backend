package com.arquisoft.config;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
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
    private final GestorTraza gestorTraza;

    public FailedEventRetryConfig(FailedEventPublications failedEventPublications, GestorTraza gestorTraza) {
        this.failedEventPublications = failedEventPublications;
        this.gestorTraza = gestorTraza;
    }

    @Scheduled(fixedDelayString = "${arquisoft.events.failed-retry-interval:PT5M}")
    public void retryFailedEvents() {
        try (var alcance = gestorTraza.abrir(SolicitudTraza.paraProgramado())) {
            log.debug("Verificando eventos de dominio con estado FAILED para reintento (traza {})",
                    alcance.correlacionId());
            failedEventPublications.resubmit(
                    ResubmissionOptions.defaults().withMinAge(Duration.ofMinutes(2))
            );
        }
    }
}
