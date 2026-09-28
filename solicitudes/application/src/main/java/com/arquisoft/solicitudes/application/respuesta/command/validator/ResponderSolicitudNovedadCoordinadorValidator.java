package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface ResponderSolicitudNovedadCoordinadorValidator {

    void validarExistencia(UUID solicitud, boolean existeSolicitud,
                           UUID remitenteUsuario, UsuarioDomain remitente,
                           UUID coordinadorUsuario, UsuarioDomain coordinador);

    void validarReglasDeNegocio(UUID solicitud, String tipoProyectado,
                                UUID destinatarioUsuarioProyectado, UUID solicitante,
                                boolean yaRespondida);
}
