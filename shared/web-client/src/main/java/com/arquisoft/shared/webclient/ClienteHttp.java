package com.arquisoft.shared.webclient;

public interface ClienteHttp {

    <R> RespuestaHttp<R> ejecutar(SolicitudHttp solicitud, Class<R> tipoRespuesta);
}
