package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;

import java.util.UUID;

public interface ModificarEstadoRespuestaNovedadCoordinadorValidator {

    void validar(ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada,
                 boolean existeSolicitud, String tipoProyectado,
                 UUID destinatarioUsuarioProyectado,
                 boolean existeRespuesta, String estadoActual);
}
