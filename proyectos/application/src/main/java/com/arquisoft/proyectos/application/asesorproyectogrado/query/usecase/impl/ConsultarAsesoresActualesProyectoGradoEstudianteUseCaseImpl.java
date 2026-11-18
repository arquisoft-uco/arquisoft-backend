package com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport.AsesorActualProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.usecase.ConsultarAsesoresActualesProyectoGradoEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresActualesProyectoGradoEstudianteUseCaseImpl
        implements ConsultarAsesoresActualesProyectoGradoEstudianteUseCase {

    private final AsesorActualProyectoGradoQueryOutputPort asesorActualProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<AsesorActualProyectoGradoReadModel> ejecutar(AsesorActualProyectoGradoEstudianteCriteria criteria) {
        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTANDO_ACTUALES, criteria.estudiante());

        var resultado = asesorActualProyectoGradoQueryOutputPort.consultarPorEstudiante(criteria);

        logger.debug(AsesorProyectoGradoKey.LOG_CONSULTA_ACTUALES_COMPLETADA, resultado.size());
        return resultado;
    }
}
