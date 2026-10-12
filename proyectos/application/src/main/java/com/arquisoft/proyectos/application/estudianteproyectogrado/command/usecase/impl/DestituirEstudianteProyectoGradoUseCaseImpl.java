package com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.finder.EstudianteVinculadoFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.DestituirEstudianteProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DestituirEstudianteProyectoGradoUseCaseImpl implements DestituirEstudianteProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final EstudianteVinculadoFinder estudianteVinculadoFinder;
    private final DestituirEstudianteProyectoGradoValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(DestitucionEstudianteProyectoGradoDomain entrada) {
        logger.info(EstudianteProyectoGradoKey.LOG_DESTITUYENDO, entrada.getProyectoGrado(), entrada.getEstudiante());

        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        var vinculado = estudianteVinculadoFinder.obtener(entrada);

        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_DESTITUIR, !proyecto.esVacio(), vinculado);

        validator.validar(entrada, proyecto, vinculado);

        estudianteProyectoGradoOutputPort.desvincular(entrada.getProyectoGrado(), entrada.getEstudiante());

        logger.info(EstudianteProyectoGradoKey.LOG_DESTITUIDO, entrada.getProyectoGrado(), entrada.getEstudiante());
    }
}
