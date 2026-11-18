package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.CoordinadorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarCoordinadoresProyectoGradoEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresProyectoGradoEstudianteUseCaseImpl
        implements ConsultarCoordinadoresProyectoGradoEstudianteUseCase {

    private final CoordinadorProyectoGradoQueryOutputPort coordinadorProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CoordinadorProyectoGradoReadModel> ejecutar(CoordinadorProyectoGradoEstudianteCriteria criteria) {
        logger.debug(ProyectoGradoKey.LOG_CONSULTANDO_COORDINADORES, criteria.estudiante());

        var resultado = coordinadorProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria);

        logger.debug(ProyectoGradoKey.LOG_CONSULTA_COORDINADORES_COMPLETADA, resultado.size());
        return resultado;
    }
}
