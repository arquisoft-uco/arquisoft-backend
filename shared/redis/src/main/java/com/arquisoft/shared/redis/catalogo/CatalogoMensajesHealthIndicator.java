package com.arquisoft.shared.redis.catalogo;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

/**
 * Expone el estado del catálogo en {@code /actuator/health}.
 *
 * <p>La degradación por caída de Redis no rechaza peticiones ni tumba la aplicación, así que sin
 * esto solo quedaría constancia en el log. Aquí se vuelve consultable.
 */
public class CatalogoMensajesHealthIndicator implements HealthIndicator {

    private static final String DETALLE_ESTADO = "estado";
    private static final String DETALLE_CLAVES_EN_CACHE = "clavesEnCache";
    private static final String ESTADO_SANO = "sano";
    private static final String ESTADO_DEGRADADO = "degradado-sirviendo-desde-cache";

    private final CatalogoMensajesRedis catalogo;

    public CatalogoMensajesHealthIndicator(CatalogoMensajesRedis catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public Health health() {
        Health.Builder constructor = catalogo.estaDegradado() ? Health.down() : Health.up();

        return constructor
                .withDetail(DETALLE_ESTADO, catalogo.estaDegradado() ? ESTADO_DEGRADADO : ESTADO_SANO)
                .withDetail(DETALLE_CLAVES_EN_CACHE, catalogo.clavesEnCache())
                .build();
    }
}
