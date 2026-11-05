package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.ResolucionSesionOutputPort;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.ResolucionSesionPdpOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;

@Primary
@Component
public class ResolucionSesionConCacheOutputAdapter implements ResolucionSesionOutputPort {

    private static final String ALGORITMO_HUELLA = "SHA-256";

    private final ResolucionSesionPdpOutputPort pdp;
    private final AppLogger logger;
    private final boolean cacheHabilitada;
    private final IdentidadesEnMemoria identidades;

    public ResolucionSesionConCacheOutputAdapter(ResolucionSesionPdpOutputPort pdp, SesionProperties propiedades,
                                                 AppLogger logger) {
        this.pdp = pdp;
        this.logger = logger;
        this.cacheHabilitada = propiedades.cache().habilitada();
        this.identidades = new IdentidadesEnMemoria(
                Math.max(propiedades.cache().maxSize(), 1), propiedades.cache().ttl(), Clock.systemUTC());
    }

    @Override
    public ResolucionSesion resolver(String credencial) {
        if (!cacheHabilitada) {
            return pdp.resolver(credencial);
        }
        var huella = huella(credencial);
        var cacheada = identidades.obtener(huella);
        logger.debug(ResolucionSesionKey.LOG_IDENTIDAD_CACHE, cacheada.isPresent());
        if (cacheada.isPresent()) {
            return cacheada.get();
        }
        var resolucion = pdp.resolver(credencial);
        if (resolucion instanceof ResolucionSesion.Resuelta resuelta) {
            identidades.guardar(huella, resuelta);
        }
        return resolucion;
    }

    static String huella(String credencial) {
        try {
            var resumen = MessageDigest.getInstance(ALGORITMO_HUELLA);
            return HexFormat.of().formatHex(resumen.digest(credencial.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
