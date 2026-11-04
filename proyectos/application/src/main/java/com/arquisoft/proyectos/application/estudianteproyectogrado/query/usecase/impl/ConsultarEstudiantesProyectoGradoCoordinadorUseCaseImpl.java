package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesProyectoGradoCoordinadorUseCase;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesProyectoGradoCoordinadorValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesProyectoGradoCoordinadorUseCaseImpl
        implements ConsultarEstudiantesProyectoGradoCoordinadorUseCase {

    private final ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;
    private final ConsultarEstudiantesProyectoGradoCoordinadorValidator validator;
    private final EstudianteProyectoGradoQueryOutputPort estudianteProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstudianteProyectoGradoReadModel> ejecutar(EstudianteProyectoGradoCoordinadorCriteria criteria) {
        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO, criteria.proyectoGrado(), criteria.coordinador());

        var coordinadorDelProyecto = proyectoGradoCoordinadorQueryFinder.obtener(criteria.proyectoGrado());

        validator.validar(criteria.proyectoGrado(), coordinadorDelProyecto, criteria.coordinador());

        var resultado = estudianteProyectoGradoQueryOutputPort.consultarPorProyecto(criteria.proyectoGrado());

        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
