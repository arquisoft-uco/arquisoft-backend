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

    /**
     * Reintenta periódicamente los eventos de dominio con estado FAILED.
     *
     * El staleness checker de Spring Modulith solo marca eventos como FAILED cuando llevan
     * demasiado tiempo en PROCESSING/RESUBMITTED — nunca los reintenta. Este scheduler
     * cubre el caso donde RabbitMQ cae mientras la app está corriendo: el evento queda
     * FAILED inmediatamente y aquí se reintenta cuando el broker vuelve a estar disponible.
     *
     * withMinAge(2m): evita reintentar un evento que acaba de fallar milisegundos atrás,
     * dando tiempo al broker para recuperarse entre intentos.
     */
    @Scheduled(fixedDelayString = "${arquisoft.events.failed-retry-interval:PT5M}")
    public void retryFailedEvents() {
        log.debug("Verificando eventos de dominio con estado FAILED para reintento...");
        failedEventPublications.resubmit(
                ResubmissionOptions.defaults().withMinAge(Duration.ofMinutes(2))
        );
    }
}
