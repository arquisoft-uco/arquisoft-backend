package com.arquisoft.solicitudes.application.respuesta.command.validator;

import java.util.UUID;

public interface ResponderSolicitudNovedadCoordinadorValidator {

    void validar(UUID solicitud, boolean existe, String tipoProyectado,
                 UUID destinatarioUsuarioProyectado, UUID solicitante, boolean yaRespondida);
}
