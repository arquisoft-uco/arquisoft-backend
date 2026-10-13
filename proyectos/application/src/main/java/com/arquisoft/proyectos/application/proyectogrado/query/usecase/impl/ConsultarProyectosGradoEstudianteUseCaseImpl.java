package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarProyectosGradoEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarProyectosGradoEstudianteUseCaseImpl implements ConsultarProyectosGradoEstudianteUseCase {

    private final ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ProyectoGradoReadModel> ejecutar(ProyectoGradoEstudianteCriteria criteria) {
        logger.debug(ProyectoGradoKey.LOG_CONSULTANDO_ESTUDIANTE, criteria.estudiante());

        var resultado = proyectoGradoQueryOutputPort.consultarPorEstudiante(criteria);

        logger.debug(ProyectoGradoKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, resultado.size());
        return resultado;
    }
}
