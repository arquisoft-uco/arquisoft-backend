package com.arquisoft.solicitudes.application.destinatario.command.secondaryport;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;

import java.util.Optional;
import java.util.UUID;

public interface DestinatarioOutputPort {

    Optional<UUID> buscarIdPorUsuario(UUID usuarioId);

    void registrar(DestinatarioEntity destinatario);
}
