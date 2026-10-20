package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.mapper.PepSolicitudMapper;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.http.PepRestTemplateConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class PepDecisionAccesoOutputAdapter implements DecisionAccesoOutputPort {

    private static final String RUTA_DECISION = "/internal/v1/embedded-access-decisions";

    private final RestTemplate restTemplate;
    private final PepProperties propiedades;
    private final AppLogger logger;

    public PepDecisionAccesoOutputAdapter(
            @Qualifier(PepRestTemplateConfig.BEAN) RestTemplate restTemplate,
            PepProperties propiedades,
            AppLogger logger) {
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        var peticion = new HttpEntity<>(
                PepSolicitudMapper.toCuerpo(propiedades, solicitud),
                PepSolicitudMapper.toCabeceras(propiedades, solicitud));
        var url = propiedades.url().replaceAll("/+$", "") + RUTA_DECISION;
        RuntimeException ultimoFallo = null;

        for (var intento = 0; intento <= propiedades.maxRetries(); intento++) {
            if (intento > 0) {
                esperarAntesDeReintentar();
            }
            try {
                var respuesta = restTemplate.exchange(url, HttpMethod.POST, peticion, Void.class);
                if (respuesta.getStatusCode().is2xxSuccessful()) {
                    var idDecision = respuesta.getHeaders().getFirst(PepSolicitudMapper.CABECERA_DECISION_ID);
                    return new DecisionAcceso.Permitida(UtilTexto.aplicarTrim(idDecision));
                }
                throw noDisponible(String.valueOf(respuesta.getStatusCode().value()), null);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                    return new DecisionAcceso.NoAutenticada();
                }
                if (e.getStatusCode().value() == HttpStatus.FORBIDDEN.value()) {
                    return new DecisionAcceso.Denegada();
                }
                throw noDisponible(String.valueOf(e.getStatusCode().value()), e);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                ultimoFallo = noDisponible(e.getClass().getSimpleName(), e);
            }
        }
        throw ultimoFallo;
    }

    private ProveedorAutorizacionNoDisponibleException noDisponible(String motivo, Exception causa) {
        logger.error(AutorizacionKey.LOG_PEP_NO_DISPONIBLE, motivo, UtilObjeto.esNulo(causa) ? motivo : causa.getMessage());
        return new ProveedorAutorizacionNoDisponibleException(
                Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE), causa);
    }

    private void esperarAntesDeReintentar() {
        try {
            Thread.sleep(propiedades.retryBackoff());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProveedorAutorizacionNoDisponibleException(
                    Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE), e);
        }
    }
}
