package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.catalogoparametros.application.command.finder.ParametroCatalogoFinder;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ParametroCatalogoNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class CuotaGlobalSolicitudes {

    static final String NOMBRE_PARAMETRO = "RATE_LIMIT_SOLICITUDES_POR_MINUTO";

    private final ParametroCatalogoFinder parametroCatalogoFinder;
    private final LimiteSolicitudesProperties properties;
    private final AppLogger appLogger;

    private final AtomicReference<CuotaResuelta> ultima = new AtomicReference<>();

    public int vigente() {
        var crudo = leerCrudo();
        var actual = ultima.get();
        if (UtilObjeto.noEsNulo(actual) && Objects.equals(actual.crudo(), crudo)) {
            return actual.cuota();
        }

        var nueva = resolver(crudo);
        var anterior = ultima.getAndSet(nueva);
        if (UtilObjeto.esNulo(anterior) || !Objects.equals(anterior.crudo(), nueva.crudo())) {
            registrar(anterior, nueva);
        }
        return nueva.cuota();
    }

    // Red de seguridad por si se borra el default de application.yml: con el default presente el
    // modulo nunca lanza y esta captura no cuesta una excepcion por peticion.
    private String leerCrudo() {
        try {
            return parametroCatalogoFinder.obtener(NOMBRE_PARAMETRO);
        } catch (ParametroCatalogoNoDisponibleException e) {
            return null;
        }
    }

    private CuotaResuelta resolver(String crudo) {
        var respaldo = properties.requestsPerMinute();
        if (UtilObjeto.esNulo(crudo)) {
            return new CuotaResuelta(null, respaldo, false);
        }
        try {
            var cuota = Integer.parseInt(UtilTexto.aplicarTrim(crudo));
            if (cuota >= SeguridadLimits.LimiteSolicitudes.CUOTA_MIN) {
                return new CuotaResuelta(crudo, cuota, true);
            }
        } catch (NumberFormatException e) {
            return new CuotaResuelta(crudo, respaldo, false);
        }
        return new CuotaResuelta(crudo, respaldo, false);
    }

    private void registrar(CuotaResuelta anterior, CuotaResuelta nueva) {
        if (UtilObjeto.esNulo(nueva.crudo())) {
            appLogger.warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_AUSENTE, NOMBRE_PARAMETRO, nueva.cuota());
        } else if (!nueva.valida()) {
            appLogger.warn(LimiteSolicitudesKey.LOG_CUOTA_CATALOGO_INVALIDA,
                    NOMBRE_PARAMETRO, nueva.crudo(), nueva.cuota());
        }
        if (UtilObjeto.esNulo(anterior) || anterior.cuota() != nueva.cuota()) {
            appLogger.info(LimiteSolicitudesKey.LOG_CUOTA_GLOBAL_ACTUALIZADA, nueva.cuota());
        }
    }

    private record CuotaResuelta(String crudo, int cuota, boolean valida) {
    }
}
