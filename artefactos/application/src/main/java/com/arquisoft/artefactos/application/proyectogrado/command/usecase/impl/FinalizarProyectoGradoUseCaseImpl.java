package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.FinalizacionProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.FinalizarProyectoGradoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.FinalizarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarProyectoGradoUseCaseImpl implements FinalizarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final FinalizarProyectoGradoValidator finalizarProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public FinalizacionProyectoGradoResult ejecutar(FinalizacionProyectoGradoDomain finalizacion) {
        var proyecto = proyectoGradoPorIdFinder.obtener(finalizacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_FINALIZAR, !proyecto.esVacio(), proyecto.estaFinalizado());

        finalizarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(finalizacion.getProyectoGrado(), !proyecto.esVacio()));

        if (proyecto.estaFinalizado()) {
            return FinalizacionProyectoGradoResultMapper.toResultYaFinalizado(finalizacion);
        }

        proyectoGradoOutputPort.marcarFinalizado(finalizacion.getProyectoGrado(), finalizacion.getFinalizadoEn());
        return FinalizacionProyectoGradoResultMapper.toResultFinalizado(finalizacion);
    }
}
