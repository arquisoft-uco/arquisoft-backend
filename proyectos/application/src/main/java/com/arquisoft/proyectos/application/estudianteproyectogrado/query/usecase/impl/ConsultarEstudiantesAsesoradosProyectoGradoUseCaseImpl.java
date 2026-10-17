package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarEstudiantesAsesoradosProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarEstudiantesAsesoradosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesAsesoradosProyectoGradoUseCaseImpl
        implements ConsultarEstudiantesAsesoradosProyectoGradoUseCase {

    private final ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;
    private final AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;
    private final ConsultarEstudiantesAsesoradosProyectoGradoValidator validator;
    private final EstudianteProyectoGradoQueryOutputPort estudianteProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstudianteAsesoradoProyectoGradoReadModel> ejecutar(
            EstudianteAsesoradoProyectoGradoCriteria criteria) {
        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO_ASESORADOS,
                criteria.proyectoGrado(), criteria.asesor());

        var proyectoExiste = proyectoGradoExisteQueryFinder.obtener(criteria.proyectoGrado());
        var asesorAsignado = asesorAsignadoQueryFinder.obtener(
                new AsignacionAsesorConsultada(criteria.asesor(), criteria.proyectoGrado()));

        validator.validar(criteria, proyectoExiste, asesorAsignado);

        var resultado = estudianteProyectoGradoQueryOutputPort
                .consultarAsesoradosVigentesPorProyecto(criteria.proyectoGrado());

        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTA_ASESORADOS_COMPLETADA, resultado.size());

        return resultado;
    }
}
