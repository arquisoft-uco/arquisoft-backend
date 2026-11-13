package com.arquisoft.artefactos.application.proyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.artefactos.application.proyectogrado.command.result.mapper.CancelacionProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelarProyectoGradoUseCaseImpl implements CancelarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final CancelarProyectoGradoValidator cancelarProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public CancelacionProyectoGradoResult ejecutar(CancelacionProyectoGradoDomain cancelacion) {
        var proyecto = proyectoGradoPorIdFinder.obtener(cancelacion.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CANCELAR, !proyecto.esVacio(), proyecto.estaCancelado());

        cancelarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(cancelacion.getProyectoGrado(), !proyecto.esVacio()));

        if (proyecto.estaCancelado()) {
            return CancelacionProyectoGradoResultMapper.toResultYaCancelado(cancelacion);
        }

        proyectoGradoOutputPort.marcarCancelado(cancelacion.getProyectoGrado(), cancelacion.getCanceladoEn());
        return CancelacionProyectoGradoResultMapper.toResultCancelado(cancelacion);
    }
}
