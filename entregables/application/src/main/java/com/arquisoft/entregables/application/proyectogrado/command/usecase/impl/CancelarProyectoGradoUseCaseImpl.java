package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.result.mapper.CancelacionProyectoGradoResultMapper;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.CancelarProyectoGradoUseCase;
import com.arquisoft.entregables.application.proyectogrado.command.validator.CancelarProyectoGradoValidator;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
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
    public CancelacionProyectoGradoResult ejecutar(CancelacionProyectoGradoDomain entrada) {
        var proyecto = proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CANCELAR, entrada.getProyectoGrado(), !proyecto.esVacio());

        cancelarProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(entrada.getProyectoGrado(), !proyecto.esVacio()));

        if (proyecto.getEstadoProyecto() == EstadoProyecto.CANCELADO) {
            return CancelacionProyectoGradoResultMapper.toResultDuplicado(entrada);
        }

        proyectoGradoOutputPort.actualizarEstado(entrada.getProyectoGrado(), entrada.getEstadoNuevo().getId());
        return CancelacionProyectoGradoResultMapper.toResultCancelado(entrada);
    }
}
