package com.arquisoft.entregables.application.artefacto.query.usecase.impl;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.entregables.application.artefacto.query.usecase.ConsultarVistaPreviaPaqueteUseCase;
import com.arquisoft.entregables.application.artefacto.query.validator.ConsultarVistaPreviaPaqueteValidator;
import com.arquisoft.entregables.application.asesorproyectogrado.query.finder.AsesorVigenteProyectoGradoQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarVistaPreviaPaqueteUseCaseImpl implements ConsultarVistaPreviaPaqueteUseCase {

    private final AsesorVigenteProyectoGradoQueryFinder asesorVigenteProyectoGradoQueryFinder;
    private final ConsultarVistaPreviaPaqueteValidator consultarVistaPreviaPaqueteValidator;
    private final ArtefactoQueryOutputPort artefactoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ArtefactoVistaPreviaReadModel> ejecutar(VistaPreviaPaqueteCriteria entrada) {
        logger.debug(ArtefactoKey.LOG_CONSULTANDO_VISTA_PREVIA, entrada.proyectoGrado());

        var asesorVigente = asesorVigenteProyectoGradoQueryFinder.obtener(entrada.proyectoGrado());
        consultarVistaPreviaPaqueteValidator.validar(entrada.proyectoGrado(), asesorVigente, entrada.asesor());
        var artefactos = artefactoQueryOutputPort.consultarVigentesPorProyecto(entrada.proyectoGrado());

        logger.debug(ArtefactoKey.LOG_VISTA_PREVIA_CONSULTADA, entrada.proyectoGrado(), artefactos.size());
        return artefactos;
    }
}
