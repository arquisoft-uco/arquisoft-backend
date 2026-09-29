package com.arquisoft.usuarios.application.coordinador.command.usecase.impl;

import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.usuarios.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.usuarios.application.coordinador.command.validator.AgregarCoordinadorValidator;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.event.CoordinadorAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorUseCaseImpl implements AgregarCoordinadorUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    private final AgregarCoordinadorValidator agregarCoordinadorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var coordinador = coordinadorPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarCoordinadorKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !coordinador.esVacio(), coordinador.estaEliminado());

        agregarCoordinadorValidator.validar(usuario.getId(), coordinador);

        if (coordinador.esVacio()) {
            coordinadorOutputPort.guardar(CoordinadorMapper.toEntity(CoordinadorDomain.crear(usuario.getId())));
        } else {
            reactivar(coordinador);
        }

        eventPublisher.publish(new CoordinadorAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(CoordinadorDomain coordinador) {
        coordinador.reactivar();
        coordinadorOutputPort.reactivar(coordinador.getUsuario());
        logger.info(AgregarCoordinadorKey.LOG_REACTIVADO, coordinador.getUsuario());
    }
}
