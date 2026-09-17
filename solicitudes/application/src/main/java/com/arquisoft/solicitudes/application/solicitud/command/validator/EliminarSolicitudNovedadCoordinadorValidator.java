package com.arquisoft.solicitudes.application.solicitud.command.validator;

import java.util.UUID;

public interface EliminarSolicitudNovedadCoordinadorValidator {

    void validar(UUID solicitud, boolean existe, UUID remitenteUsuarioProyectado,
                 String tipoProyectado, UUID solicitante, boolean tieneRespuestas);
}
