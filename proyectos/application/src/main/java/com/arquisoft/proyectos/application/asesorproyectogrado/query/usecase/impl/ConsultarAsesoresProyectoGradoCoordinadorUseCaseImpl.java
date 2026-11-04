package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresProyectoGradoCoordinadorUseCase;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.ConsultarAsesoresProyectoGradoCoordinadorValidator;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoCoordinadorQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresProyectoGradoCoordinadorUseCaseImpl
        implements ConsultarAsesoresProyectoGradoCoordinadorUseCase {

    private final ProyectoGradoCoordinadorQueryFinder proyectoGradoCoordinadorQueryFinder;
    private final ConsultarAsesoresProyectoGradoCoordinadorValidator validator;
    private final AsesorProyectoGradoQueryOutputPort asesorProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<AsesorProyectoGradoReadModel> ejecutar(AsesorProyectoGradoCoordinadorCriteria criteria) {
        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTANDO, criteria.proyectoGrado(), criteria.coordinador());

        var coordinadorDelProyecto = proyectoGradoCoordinadorQueryFinder.obtener(criteria.proyectoGrado());

        validator.validar(criteria.proyectoGrado(), coordinadorDelProyecto, criteria.coordinador());

        var resultado = asesorProyectoGradoQueryOutputPort.consultarPorProyecto(criteria.proyectoGrado());

        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
