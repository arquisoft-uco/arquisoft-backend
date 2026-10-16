package com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.finder.EstudianteVinculadoVigenteQueryFinder;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport.EstudianteProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarCompanerosProyectoGradoUseCase;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.ConsultarCompanerosProyectoGradoValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCompanerosProyectoGradoUseCaseImpl implements ConsultarCompanerosProyectoGradoUseCase {

    private final ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;
    private final EstudianteVinculadoVigenteQueryFinder estudianteVinculadoVigenteQueryFinder;
    private final ConsultarCompanerosProyectoGradoValidator validator;
    private final EstudianteProyectoGradoQueryOutputPort estudianteProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CompaneroProyectoGradoReadModel> ejecutar(CompaneroProyectoGradoCriteria criteria) {
        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTANDO_COMPANEROS,
                criteria.proyectoGrado(), criteria.estudiante());

        var proyectoExiste = proyectoGradoExisteQueryFinder.obtener(criteria.proyectoGrado());
        var vinculado = estudianteVinculadoVigenteQueryFinder.obtener(criteria);

        validator.validar(criteria, proyectoExiste, vinculado);

        var resultado = estudianteProyectoGradoQueryOutputPort
                .consultarCompanerosVigentesPorProyecto(criteria.proyectoGrado());

        logger.debug(EstudianteProyectoGradoKey.LOG_CONSULTA_COMPANEROS_COMPLETADA, resultado.size());

        return resultado;
    }
}
