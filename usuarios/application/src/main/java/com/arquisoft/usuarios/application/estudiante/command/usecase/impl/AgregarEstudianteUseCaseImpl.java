package com.arquisoft.usuarios.application.estudiante.command.usecase.impl;

import com.arquisoft.usuarios.application.estudiante.command.finder.EstudianteUsuarioExisteFinder;
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
    private final EstudianteUsuarioExisteFinder estudianteUsuarioExisteFinder;
    private final AgregarEstudianteValidator agregarEstudianteValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var yaEsEstudiante = estudianteUsuarioExisteFinder.obtener(usuario.getId());
        logger.debug(AgregarEstudianteKey.LOG_VERIFICACION_AGREGAR, usuario.getId(), yaEsEstudiante);

        agregarEstudianteValidator.validar(usuario.getId(), yaEsEstudiante);

        var estudiante = EstudianteDomain.crear(usuario.getId());
        estudianteOutputPort.guardar(EstudianteMapper.toEntity(estudiante));

        eventPublisher.publish(new EstudianteAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }
}
