package com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.secondaryport.EstadoProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.usecase.ConsultarHistorialEstadosProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.ConsultarHistorialEstadosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarHistorialEstadosProyectoGradoUseCaseImpl implements ConsultarHistorialEstadosProyectoGradoUseCase {

    private final ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;
    private final AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;
    private final ConsultarHistorialEstadosProyectoGradoValidator validator;
    private final EstadoProyectoGradoQueryOutputPort estadoProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoProyectoGradoReadModel> ejecutar(HistorialEstadosProyectoGradoCriteria criteria) {
        logger.debug(EstadoProyectoGradoKey.LOG_CONSULTANDO_HISTORIAL, criteria.proyectoGrado(), criteria.usuario());

        var coordinadorDelProyecto = proyectoGradoCoordinadorQueryFinder.obtener(criteria.proyectoGrado());
        var asesorAsignado = asesorAsignadoQueryFinder.obtener(
                new AsignacionAsesorConsultada(criteria.usuario(), criteria.proyectoGrado()));

        validator.validar(criteria, coordinadorDelProyecto, asesorAsignado);

        var resultado = estadoProyectoGradoQueryOutputPort.consultarPorProyecto(criteria.proyectoGrado());
        logger.debug(EstadoProyectoGradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());
        return resultado;
    }
}
