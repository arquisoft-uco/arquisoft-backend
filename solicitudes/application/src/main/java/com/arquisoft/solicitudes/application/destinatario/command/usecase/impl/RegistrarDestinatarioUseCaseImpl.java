package com.arquisoft.solicitudes.application.destinatario.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarDestinatarioUseCaseImpl implements RegistrarDestinatarioUseCase {

    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final DestinatarioOutputPort destinatarioOutputPort;

    @Override
    public UUID ejecutar(DestinatarioDomain destinatario) {
        var destinatarioId = destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario());
        if (!UtilUUID.esPorDefecto(destinatarioId)) {
            return destinatarioId;
        }
        destinatarioOutputPort.registrar(DestinatarioMapper.toEntity(destinatario));
        return destinatario.getId();
    }
}
