package com.arquisoft.shared.webclient.impl;

import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

public class ClienteHttpRestClient implements ClienteHttp {

    private final RestClient restClient;

    public ClienteHttpRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <R> RespuestaHttp<R> ejecutar(SolicitudHttp solicitud, Class<R> tipoRespuesta) {
        try {
            var peticion = restClient.method(HttpMethod.valueOf(solicitud.metodo().name()))
                    .uri(solicitud.ruta())
                    .headers(cabeceras -> solicitud.cabeceras().forEach(cabeceras::set));
            var conCuerpo = adjuntarCuerpo(peticion, solicitud);
            return conCuerpo.exchange((request, response) -> {
                var estado = response.getStatusCode().value();
                var exitosa = response.getStatusCode().is2xxSuccessful();
                var cuerpo = exitosa && !Void.class.equals(tipoRespuesta) ? response.bodyTo(tipoRespuesta) : null;
                return new RespuestaHttp<>(estado, cuerpo);
            });
        } catch (RestClientException e) {
            throw new ClienteHttpTransporteException(e.getClass().getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private RestClient.RequestBodySpec adjuntarCuerpo(RestClient.RequestBodySpec peticion, SolicitudHttp solicitud) {
        if (solicitud.cuerpo() == null) {
            return peticion;
        }
        if (TipoContenido.FORM.equals(solicitud.tipoContenido())) {
            var formulario = new LinkedMultiValueMap<String, String>();
            ((Map<String, String>) solicitud.cuerpo()).forEach(formulario::add);
            return peticion.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(formulario);
        }
        return peticion.contentType(MediaType.APPLICATION_JSON).body(solicitud.cuerpo());
    }
}
