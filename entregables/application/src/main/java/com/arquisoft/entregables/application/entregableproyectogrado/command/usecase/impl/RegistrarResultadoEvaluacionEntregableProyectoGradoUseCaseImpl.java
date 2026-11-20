package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.mapper.RegistroResultadoEvaluacionEntregableProyectoGradoResultMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarPaqueteResultadoEvaluacionValidator;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.RegistrarResultadoEvaluacionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.EstadoActualEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper.EstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregablePorEntregableFinder;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoDeEntregableFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoAprobadoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoRechazadoEvent;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarResultadoEvaluacionEntregableProyectoGradoUseCaseImpl
        implements RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase {

    private final EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;
    private final ProyectoGradoBloqueadoDeEntregableFinder proyectoGradoBloqueadoDeEntregableFinder;
    private final EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;
    private final EstadoActualEntregableProyectoGradoFinder estadoActualEntregableProyectoGradoFinder;
    private final PaqueteEntregablePorEntregableFinder paqueteEntregablePorEntregableFinder;
    private final RegistrarResultadoEvaluacionEntregableProyectoGradoValidator
            registrarResultadoEvaluacionEntregableProyectoGradoValidator;
    private final ConfirmarPaqueteResultadoEvaluacionValidator confirmarPaqueteResultadoEvaluacionValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public RegistroResultadoEvaluacionEntregableProyectoGradoResult ejecutar(
            ResultadoEvaluacionEntregableProyectoGradoDomain resultado) {
        var proyecto = proyectoGradoBloqueadoDeEntregableFinder.obtener(resultado.getEntregableProyectoGrado());
        var entregable = entregableProyectoGradoPorIdFinder.obtener(resultado.getEntregableProyectoGrado());
        var estadoActual = estadoActualEntregableProyectoGradoFinder.obtener(resultado.getEntregableProyectoGrado());
        var paquete = paqueteEntregablePorEntregableFinder.obtener(resultado.getEntregableProyectoGrado());
        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_REGISTRAR_RESULTADO,
                !entregable.esVacio(), estadoActual.getEstadoEntregable().getId(),
                !proyecto.esVacio(), proyecto.getEstadoProyecto().getId(), !paquete.esVacio());

        registrarResultadoEvaluacionEntregableProyectoGradoValidator.validar(
                resultado, entregable, estadoActual, proyecto);

        if (!estadoActual.admiteResultadoEvaluacion() || proyecto.estaCerrado()) {
            return RegistroResultadoEvaluacionEntregableProyectoGradoResultMapper.toResultDescartado(
                    resultado.getEntregableProyectoGrado(), estadoActual.getEstadoEntregable().getId(),
                    proyecto.getEstadoProyecto().getId());
        }

        confirmarPaqueteResultadoEvaluacionValidator.validar(resultado, paquete);

        var nuevoEstado = EstadoEntregableProyectoGradoDomain.crearConEstado(
                resultado.getEntregableProyectoGrado(), resultado.getEstadoEntregable());
        estadoEntregableProyectoGradoOutputPort.registrar(EstadoEntregableProyectoGradoMapper.toEntity(nuevoEstado));

        if (resultado.esRechazado()) {
            eventPublisher.publish(new EntregableProyectoGradoRechazadoEvent(
                    entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion()));
        } else {
            eventPublisher.publish(new EntregableProyectoGradoAprobadoEvent(
                    entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion(),
                    resultado.getEstadoEntregable(), paquete.getBucket(), paquete.getClaveObjeto(),
                    paquete.getNombreArchivo()));
        }

        return RegistroResultadoEvaluacionEntregableProyectoGradoResultMapper.toResultRegistrado(
                resultado.getEntregableProyectoGrado(), resultado.getEstadoEntregable().getId());
    }
}
