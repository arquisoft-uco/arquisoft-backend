package com.arquisoft.solicitudes.application.respuesta.command.secondaryport;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.entity.RespuestaEntity;

import java.util.Optional;
import java.util.UUID;

public interface RespuestaOutputPort {

    void registrar(RespuestaEntity respuesta);

    boolean existePorSolicitud(UUID solicitudId);

    Optional<String> buscarEstadoPorSolicitud(UUID solicitudId);

    void eliminarPorSolicitud(UUID solicitudId);
}
