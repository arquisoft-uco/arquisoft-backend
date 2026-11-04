package com.arquisoft.proyectos.application.estadoproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.EstadoProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.mapper.EstadoProyectoGradoMapper;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.usecase.AsignarEstadoInicialProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estadoproyectogrado.command.validator.AsignarEstadoInicialProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialProyectoGradoUseCaseImpl implements AsignarEstadoInicialProyectoGradoUseCase {

    private final EstadoProyectoGradoOutputPort estadoProyectoGradoOutputPort;
    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AsignarEstadoInicialProyectoGradoValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EstadoProyectoGradoDomain estadoInicial) {
        var proyectoExiste = proyectoGradoExisteFinder.obtener(estadoInicial.getProyectoGrado());

        validator.validar(estadoInicial.getProyectoGrado(), proyectoExiste);

        estadoProyectoGradoOutputPort.registrar(EstadoProyectoGradoMapper.toEntity(estadoInicial));

        logger.debug(EstadoProyectoGradoKey.LOG_CREADO,
                estadoInicial.getId(),
                estadoInicial.getProyectoGrado(),
                estadoInicial.getEstadoProyecto().getNombre());
    }
}
