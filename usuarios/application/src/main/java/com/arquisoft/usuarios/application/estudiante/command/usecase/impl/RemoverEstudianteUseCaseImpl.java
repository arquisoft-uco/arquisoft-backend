package com.arquisoft.usuarios.application.estudiante.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverEstudianteKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import com.arquisoft.usuarios.application.estudiante.command.validator.RemoverEstudianteValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.event.EstudianteRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteUseCaseImpl implements RemoverEstudianteUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverEstudianteValidator removerEstudianteValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EstudianteDomain entrada) {
        logger.info(RemoverEstudianteKey.LOG_REMOVIENDO, entrada.getUsuario());

        var estudiante = estudiantePorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverEstudianteKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !estudiante.esVacio(), estudiante.estaEliminado());

        removerEstudianteValidator.validar(entrada.getUsuario(), estudiante);

        estudiante.remover(UtilFecha.generarInstanteActual());
        estudianteOutputPort.eliminarLogica(estudiante.getUsuario(), estudiante.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(estudiante.getUsuario(), UsuariosRealmRoles.ESTUDIANTE);

        eventPublisher.publish(new EstudianteRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverEstudianteKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
