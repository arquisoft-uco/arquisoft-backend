package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.dto.SesionPdpResponseDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.mapper.PdpSesionMapper;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.config.http.SesionRestTemplateConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PdpResolucionSesionOutputAdapter implements ResolucionSesionPdpOutputPort {

    private static final String RUTA_SESION = "/api/v1/session";
    private static final String MOTIVO_RESPUESTA_INVALIDA = "respuesta-invalida";

    private final RestTemplate restTemplate;
    private final SesionProperties propiedades;
    private final AppLogger logger;

    public PdpResolucionSesionOutputAdapter(
            @Qualifier(SesionRestTemplateConfig.BEAN) RestTemplate restTemplate,
            SesionProperties propiedades,
            AppLogger logger) {
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    @Override
    public ResolucionSesion resolver(String credencial) {
        var url = propiedades.pdpUrl().replaceAll("/+$", "") + RUTA_SESION;
        var peticion = new HttpEntity<>(PdpSesionMapper.toCabeceras(credencial));
        try {
            var respuesta = restTemplate.exchange(url, HttpMethod.GET, peticion, SesionPdpResponseDTO.class);
            if (!respuesta.getStatusCode().is2xxSuccessful() || !PdpSesionMapper.tieneSujeto(respuesta.getBody())) {
                throw noDisponible(MOTIVO_RESPUESTA_INVALIDA, respuesta.getStatusCode().value(), null);
            }
            return PdpSesionMapper.toResolucion(respuesta.getBody());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                return new ResolucionSesion.SinSesion();
            }
            throw noDisponible(String.valueOf(e.getStatusCode().value()), e.getStatusCode().value(), e);
        } catch (RestClientException e) {
            throw noDisponible(e.getClass().getSimpleName(), 0, e);
        }
    }

    private ProveedorIdentidadNoDisponibleException noDisponible(String motivo, int estado, Exception causa) {
        logger.error(ResolucionSesionKey.LOG_PROVEEDOR_NO_DISPONIBLE, motivo, estado);
        return new ProveedorIdentidadNoDisponibleException(
                Mensajes.obtener(ResolucionSesionKey.ERROR_PROVEEDOR_NO_DISPONIBLE), causa);
    }
}
