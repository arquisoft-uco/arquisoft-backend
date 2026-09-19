package com.arquisoft.solicitudes.application.remitente.command.secondaryport;

import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;

import java.util.Optional;
import java.util.UUID;

public interface RemitenteOutputPort {

    Optional<UUID> buscarIdPorUsuario(UUID usuarioId);

    void registrar(RemitenteEntity remitente);
}
