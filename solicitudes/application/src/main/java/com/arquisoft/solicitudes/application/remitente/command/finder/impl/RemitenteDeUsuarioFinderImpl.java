package com.arquisoft.solicitudes.application.remitente.command.finder.impl;

import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemitenteDeUsuarioFinderImpl implements RemitenteDeUsuarioFinder {

    private final RemitenteOutputPort remitenteOutputPort;

    @Override
    public Optional<UUID> obtener(UUID usuarioId) {
        return remitenteOutputPort.buscarIdPorUsuario(usuarioId);
    }
}
