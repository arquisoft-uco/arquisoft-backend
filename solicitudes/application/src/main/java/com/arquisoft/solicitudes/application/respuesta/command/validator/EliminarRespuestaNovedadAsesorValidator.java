package com.arquisoft.solicitudes.application.respuesta.command.validator;

import java.util.UUID;

public interface EliminarRespuestaNovedadAsesorValidator {

    void validar(UUID solicitud, boolean existeSolicitud, String tipoProyectado,
                 UUID destinatarioUsuarioProyectado, UUID solicitante,
                 boolean existeRespuesta, String estadoActual);
}
