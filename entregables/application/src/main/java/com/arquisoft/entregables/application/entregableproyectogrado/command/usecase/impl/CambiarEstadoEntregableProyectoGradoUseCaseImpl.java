package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.CambiarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEntregableProyectoGradoUseCaseImpl
        implements CambiarEstadoEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    private final CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoEntregableProyectoGradoDomain cambio) {
        logger.info(EntregableProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable().getId());

        var entregable = entregableProyectoGradoPorIdFinder.obtener(cambio.getEntregableProyectoGrado());

        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_CAMBIAR_ESTADO,
                !entregable.esVacio(), entregable.getEstadoEntregable().getId());

        cambiarEstadoEntregableProyectoGradoValidator.validar(cambio, entregable);

        entregableProyectoGradoOutputPort.actualizarEstado(
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable().getId());

        logger.info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                cambio.getEntregableProyectoGrado(), entregable.getEstadoEntregable().getId(),
                cambio.getEstadoEntregable().getId());
    }
}
