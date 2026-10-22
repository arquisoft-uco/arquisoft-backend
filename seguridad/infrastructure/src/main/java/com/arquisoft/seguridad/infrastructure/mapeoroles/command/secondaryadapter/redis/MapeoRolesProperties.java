package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("security.authorization.role-expansion")
public record MapeoRolesProperties(
        @DefaultValue("PT5M") Duration refreshInterval,
        @DefaultValue("PT15M") Duration ttl,
        @DefaultValue("PT30S") Duration checkInterval,
        @DefaultValue("PT60S") Duration lockLease) {

    private static final int FACTOR_MINIMO_TTL = 2;

    // Los consume ValidadorConfiguracionSeguridad en @PostConstruct, antes del catalogo de mensajes: textos literales.
    private static final String DURACION_NO_POSITIVA = "security.authorization.role-expansion.%s debe ser positiva";
    private static final String TTL_INSUFICIENTE =
            "security.authorization.role-expansion.ttl debe ser al menos %d veces refresh-interval";
    private static final String CHECK_INTERVAL_EXCESIVO =
            "security.authorization.role-expansion.check-interval no puede superar refresh-interval";
    private static final String REFRESH_INTERVAL = "refresh-interval";
    private static final String TTL = "ttl";
    private static final String CHECK_INTERVAL = "check-interval";
    private static final String LOCK_LEASE = "lock-lease";

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        agregarSiNoPositiva(problemas, refreshInterval, REFRESH_INTERVAL);
        agregarSiNoPositiva(problemas, ttl, TTL);
        agregarSiNoPositiva(problemas, checkInterval, CHECK_INTERVAL);
        agregarSiNoPositiva(problemas, lockLease, LOCK_LEASE);
        if (!problemas.isEmpty()) {
            return problemas;
        }
        if (ttl.compareTo(refreshInterval.multipliedBy(FACTOR_MINIMO_TTL)) < 0) {
            problemas.add(TTL_INSUFICIENTE.formatted(FACTOR_MINIMO_TTL));
        }
        if (checkInterval.compareTo(refreshInterval) > 0) {
            problemas.add(CHECK_INTERVAL_EXCESIVO);
        }
        return problemas;
    }

    private static void agregarSiNoPositiva(List<String> problemas, Duration valor, String nombre) {
        if (valor.isZero() || valor.isNegative()) {
            problemas.add(DURACION_NO_POSITIVA.formatted(nombre));
        }
    }
}
