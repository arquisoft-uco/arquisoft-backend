package com.arquisoft.solicitudes.application.destinatario.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DestinatarioDeUsuarioFinderImpl implements DestinatarioDeUsuarioFinder {

    private final DestinatarioOutputPort destinatarioOutputPort;

    @Override
    public UUID obtener(UUID usuarioId) {
        return destinatarioOutputPort.buscarIdPorUsuario(usuarioId)
                .orElseGet(UtilUUID::obtenerUUIDPorDefecto);
    }
}
