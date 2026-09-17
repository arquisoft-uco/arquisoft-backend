package com.arquisoft.usuarios.application.estudiante.command.usecase.impl;

import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.usuarios.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.usuarios.application.estudiante.command.validator.AgregarEstudianteValidator;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.event.EstudianteAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteUseCaseImpl implements AgregarEstudianteUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    private final AgregarEstudianteValidator agregarEstudianteValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var estudiante = estudiantePorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarEstudianteKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !estudiante.esVacio(), estudiante.estaEliminado());

        agregarEstudianteValidator.validar(usuario.getId(), estudiante);

        if (estudiante.esVacio()) {
            estudianteOutputPort.guardar(EstudianteMapper.toEntity(EstudianteDomain.crear(usuario.getId())));
        } else {
            reactivar(estudiante);
        }

        eventPublisher.publish(new EstudianteAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(EstudianteDomain estudiante) {
        estudiante.reactivar();
        estudianteOutputPort.reactivar(estudiante.getUsuario());
        logger.info(AgregarEstudianteKey.LOG_REACTIVADO, estudiante.getUsuario());
    }
}
