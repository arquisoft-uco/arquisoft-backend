package com.arquisoft.solicitudes.application.remitente.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper.RemitenteMapper;
import com.arquisoft.solicitudes.application.remitente.command.usecase.RegistrarRemitenteUseCase;
import com.arquisoft.solicitudes.application.remitente.command.validator.RegistrarRemitenteValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarRemitenteUseCaseImpl implements RegistrarRemitenteUseCase {

    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    private final RegistrarRemitenteValidator validator;
    private final RemitenteOutputPort remitenteOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemitenteDomain remitente) {
        var usuario = usuarioPorIdFinder.obtener(remitente.getUsuario());
        logger.debug(SolicitudKey.LOG_VERIFICACION_REGISTRO_REMITENTE, remitente.getUsuario(), !usuario.esVacio());

        validator.validar(remitente, usuario);

        var remitenteId = remitenteDeUsuarioFinder.obtener(remitente.getUsuario());
        if (UtilUUID.esPorDefecto(remitenteId)) {
            remitenteOutputPort.registrar(RemitenteMapper.toEntity(remitente));
        }
    }
}
