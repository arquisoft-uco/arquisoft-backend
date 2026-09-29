package com.arquisoft.solicitudes.application.remitente.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarRemitenteUseCaseImpl implements RegistrarRemitenteUseCase {

    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final RemitenteOutputPort remitenteOutputPort;

    @Override
    public UUID ejecutar(RemitenteDomain remitente) {
        var remitenteId = remitenteDeUsuarioFinder.obtener(remitente.getUsuario());
        if (!UtilUUID.esPorDefecto(remitenteId)) {
            return remitenteId;
        }
        remitenteOutputPort.registrar(RemitenteMapper.toEntity(remitente));
        return remitente.getId();
    }
}
