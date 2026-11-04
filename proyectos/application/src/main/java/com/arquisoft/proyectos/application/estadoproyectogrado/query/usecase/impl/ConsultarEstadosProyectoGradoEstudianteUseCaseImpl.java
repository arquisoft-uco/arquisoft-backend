package com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.finder.EstudianteVinculadoEstadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport.EstadoProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarEstadosProyectoGradoEstudianteUseCase;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarEstadosProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosProyectoGradoEstudianteUseCaseImpl implements ConsultarEstadosProyectoGradoEstudianteUseCase {

    private final ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;
    private final EstudianteVinculadoEstadoProyectoGradoQueryFinder estudianteVinculadoEstadoProyectoGradoQueryFinder;
    private final ConsultarEstadosProyectoGradoEstudianteValidator validator;
    private final EstadoProyectoGradoQueryOutputPort estadoProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoProyectoGradoReadModel> ejecutar(EstadoProyectoGradoEstudianteCriteria criteria) {
        logger.debug(EstadoProyectoGradoKey.LOG_CONSULTANDO, criteria.proyectoGrado(), criteria.estudiante());

        var proyectoExiste = proyectoGradoExisteQueryFinder.obtener(criteria.proyectoGrado());
        var vinculado = estudianteVinculadoEstadoProyectoGradoQueryFinder.obtener(criteria);

        validator.validar(criteria, proyectoExiste, vinculado);

        var resultado = estadoProyectoGradoQueryOutputPort.consultarPorProyecto(criteria.proyectoGrado());
        logger.debug(EstadoProyectoGradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());
        return resultado;
    }
}
