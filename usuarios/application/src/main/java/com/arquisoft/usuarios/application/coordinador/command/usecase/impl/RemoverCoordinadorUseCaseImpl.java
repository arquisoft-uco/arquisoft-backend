package com.arquisoft.usuarios.application.coordinador.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverCoordinadorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.usecase.RemoverCoordinadorUseCase;
import com.arquisoft.usuarios.application.coordinador.command.validator.RemoverCoordinadorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.event.CoordinadorRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverCoordinadorUseCaseImpl implements RemoverCoordinadorUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverCoordinadorValidator removerCoordinadorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CoordinadorDomain entrada) {
        logger.info(RemoverCoordinadorKey.LOG_REMOVIENDO, entrada.getUsuario());

        var coordinador = coordinadorPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverCoordinadorKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !coordinador.esVacio(), coordinador.estaEliminado());

        removerCoordinadorValidator.validar(entrada.getUsuario(), coordinador);

        coordinador.remover(UtilFecha.generarInstanteActual());
        coordinadorOutputPort.eliminarLogica(coordinador.getUsuario(), coordinador.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(coordinador.getUsuario(), UsuariosRealmRoles.COORDINADOR);

        eventPublisher.publish(new CoordinadorRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverCoordinadorKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
