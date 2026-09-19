package com.arquisoft.solicitudes.application.destinatario.command.finder.impl;

import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DestinatarioDeUsuarioFinderImpl implements DestinatarioDeUsuarioFinder {

    private final DestinatarioOutputPort destinatarioOutputPort;

    @Override
    public Optional<UUID> obtener(UUID usuarioId) {
        return destinatarioOutputPort.buscarIdPorUsuario(usuarioId);
    }
}
