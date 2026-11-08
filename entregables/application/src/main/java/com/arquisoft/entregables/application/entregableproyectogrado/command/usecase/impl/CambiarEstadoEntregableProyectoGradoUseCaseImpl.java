package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.CambiarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoDeEntregableFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCanceladoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCerradoEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEntregableProyectoGradoUseCaseImpl
        implements CambiarEstadoEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    private final ProyectoGradoDeEntregableFinder proyectoGradoDeEntregableFinder;
    private final CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoEntregableProyectoGradoDomain cambio) {
        logger.info(EntregableProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable().getId());

        var entregable = entregableProyectoGradoPorIdFinder.obtener(cambio.getEntregableProyectoGrado());
        var proyecto = proyectoGradoDeEntregableFinder.obtener(cambio.getEntregableProyectoGrado());

        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_CAMBIAR_ESTADO,
                !entregable.esVacio(), entregable.getEstadoEntregable().getId(),
                !proyecto.esVacio(), proyecto.getEstadoProyecto().getId());

        cambiarEstadoEntregableProyectoGradoValidator.validar(cambio, entregable, proyecto);

        entregableProyectoGradoOutputPort.actualizarEstado(
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable().getId());

        if (cambio.getEstadoEntregable() == EstadoEntregable.CERRADO) {
            eventPublisher.publish(new EntregableProyectoGradoCerradoEvent(
                    cambio.getEntregableProyectoGrado(), entregable.getProyectoGrado(), entregable.getVersion()));
        }

        if (cambio.getEstadoEntregable() == EstadoEntregable.CANCELADO) {
            eventPublisher.publish(new EntregableProyectoGradoCanceladoEvent(
                    cambio.getEntregableProyectoGrado(), entregable.getProyectoGrado(), entregable.getVersion()));
        }

        logger.info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                cambio.getEntregableProyectoGrado(), entregable.getEstadoEntregable().getId(),
                cambio.getEstadoEntregable().getId());
    }
}
