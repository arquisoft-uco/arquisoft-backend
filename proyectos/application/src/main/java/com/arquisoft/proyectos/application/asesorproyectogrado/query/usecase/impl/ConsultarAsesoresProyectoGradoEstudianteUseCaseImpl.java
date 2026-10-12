package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.EstudianteVinculadoProyectoGradoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoEstudianteUseCase;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.ConsultarAsesoresProyectoGradoEstudianteValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresProyectoGradoEstudianteUseCaseImpl
        implements ConsultarAsesoresProyectoGradoEstudianteUseCase {

    private final ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;
    private final EstudianteVinculadoProyectoGradoQueryFinder estudianteVinculadoProyectoGradoQueryFinder;
    private final ConsultarAsesoresProyectoGradoEstudianteValidator validator;
    private final AsesorProyectoGradoQueryOutputPort asesorProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<AsesorProyectoGradoReadModel> ejecutar(AsesorProyectoGradoEstudianteCriteria criteria) {
        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTANDO_ASIGNADOS, criteria.proyectoGrado(), criteria.estudiante());

        var proyectoExiste = proyectoGradoExisteQueryFinder.obtener(criteria.proyectoGrado());
        var vinculado = estudianteVinculadoProyectoGradoQueryFinder.obtener(criteria);

        validator.validar(criteria, proyectoExiste, vinculado);

        var resultado = asesorProyectoGradoQueryOutputPort.consultarPorProyecto(criteria.proyectoGrado());
        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());
        return resultado;
    }
}
