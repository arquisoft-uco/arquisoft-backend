package com.arquisoft.solicitudes.application.destinatario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper.DestinatarioMapper;
import com.arquisoft.solicitudes.application.destinatario.command.usecase.RegistrarDestinatarioUseCase;
import com.arquisoft.solicitudes.application.destinatario.command.validator.RegistrarDestinatarioValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarDestinatarioUseCaseImpl implements RegistrarDestinatarioUseCase {

    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    private final RegistrarDestinatarioValidator validator;
    private final DestinatarioOutputPort destinatarioOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(DestinatarioDomain destinatario) {
        var usuario = usuarioPorIdFinder.obtener(destinatario.getUsuario());
        logger.debug(SolicitudKey.LOG_VERIFICACION_REGISTRO_DESTINATARIO, destinatario.getUsuario(), !usuario.esVacio());

        validator.validar(destinatario, usuario);

        var destinatarioId = destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario());
        if (UtilUUID.esPorDefecto(destinatarioId)) {
            destinatarioOutputPort.registrar(DestinatarioMapper.toEntity(destinatario));
        }
    }
}
