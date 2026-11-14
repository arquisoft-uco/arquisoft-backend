package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteDeEntregableFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.CambiarEstadoEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.EstadoActualEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper.EstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoDeEntregableFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCanceladoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCerradoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoDisponibleEvaluacionEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEntregableProyectoGradoUseCaseImpl
        implements CambiarEstadoEntregableProyectoGradoUseCase {

    private final EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;
    private final EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    private final EstadoActualEntregableProyectoGradoFinder estadoActualEntregableProyectoGradoFinder;
    private final ProyectoGradoDeEntregableFinder proyectoGradoDeEntregableFinder;
    private final AsesorVigenteDeEntregableFinder asesorVigenteDeEntregableFinder;
    private final CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoEntregableProyectoGradoDomain cambio) {
        logger.info(EntregableProyectoGradoKey.LOG_CAMBIANDO_ESTADO,
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable().getId());

        var entregable = entregableProyectoGradoPorIdFinder.obtener(cambio.getEntregableProyectoGrado());
        var estadoActual = estadoActualEntregableProyectoGradoFinder.obtener(cambio.getEntregableProyectoGrado());
        var proyecto = proyectoGradoDeEntregableFinder.obtener(cambio.getEntregableProyectoGrado());
        var asesorVigente = asesorVigenteDeEntregableFinder.obtener(cambio.getEntregableProyectoGrado());
        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_CAMBIAR_ESTADO,
                !entregable.esVacio(), estadoActual.getEstadoEntregable().getId(),
                !proyecto.esVacio(), proyecto.getEstadoProyecto().getId(), !UtilUUID.esPorDefecto(asesorVigente));

        cambiarEstadoEntregableProyectoGradoValidator.validar(cambio, entregable, estadoActual, proyecto, asesorVigente);

        var nuevoEstado = EstadoEntregableProyectoGradoDomain.crearConEstado(
                cambio.getEntregableProyectoGrado(), cambio.getEstadoEntregable());
        estadoEntregableProyectoGradoOutputPort.registrar(EstadoEntregableProyectoGradoMapper.toEntity(nuevoEstado));

        if (cambio.getEstadoEntregable() == EstadoEntregable.CERRADO) {
            eventPublisher.publish(new EntregableProyectoGradoCerradoEvent(
                    cambio.getEntregableProyectoGrado(), entregable.getProyectoGrado(), entregable.getVersion()));
        }
        if (cambio.getEstadoEntregable() == EstadoEntregable.DISPONIBLE_EVALUACION) {
            eventPublisher.publish(new EntregableProyectoGradoDisponibleEvaluacionEvent(
                    cambio.getEntregableProyectoGrado(), entregable.getProyectoGrado(), proyecto.getNombre(),
                    entregable.getVersion()));
        }
        if (cambio.getEstadoEntregable() == EstadoEntregable.CANCELADO) {
            eventPublisher.publish(new EntregableProyectoGradoCanceladoEvent(
                    cambio.getEntregableProyectoGrado(), entregable.getProyectoGrado(), entregable.getVersion()));
        }

        logger.info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO,
                cambio.getEntregableProyectoGrado(), estadoActual.getEstadoEntregable().getId(),
                cambio.getEstadoEntregable().getId());
    }
}
