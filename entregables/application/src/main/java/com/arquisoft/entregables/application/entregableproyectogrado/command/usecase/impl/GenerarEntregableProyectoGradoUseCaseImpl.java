package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableVigenteExisteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.GenerarEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper.EstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoGeneradoEvent;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenerarEntregableProyectoGradoUseCaseImpl implements GenerarEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;
    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final AsesorVigenteFinder asesorVigenteFinder;
    private final EntregableVigenteExisteFinder entregableVigenteExisteFinder;
    private final UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;
    private final GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(GeneracionEntregableProyectoGradoDomain entrada) {
        logger.info(EntregableProyectoGradoKey.LOG_GENERANDO, entrada.getProyectoGrado(), entrada.getAsesor());

        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(entrada.getProyectoGrado());
        var asesorVigente = asesorVigenteFinder.obtener(entrada.getAsesor());
        var existeVigente = entregableVigenteExisteFinder.obtener(entrada.getProyectoGrado());
        var ultimaVersion = ultimaVersionEntregableProyectoGradoFinder.obtener(entrada.getProyectoGrado());

        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_GENERAR,
                !proyecto.esVacio(), proyecto.getEstadoProyecto().getId(), asesorVigente, existeVigente,
                ultimaVersion);

        generarEntregableProyectoGradoValidator.validar(entrada, proyecto, asesorVigente, existeVigente);

        var entregable = EntregableProyectoGradoDomain.crear(
                entrada.getProyectoGrado(), ultimaVersion, entrada.getAsesor());
        entregableProyectoGradoOutputPort.registrar(EntregableProyectoGradoMapper.toEntity(entregable));
        var estadoInicial = EstadoEntregableProyectoGradoDomain.crear(entregable.getId());
        estadoEntregableProyectoGradoOutputPort.registrar(EstadoEntregableProyectoGradoMapper.toEntity(estadoInicial));

        eventPublisher.publish(new EntregableProyectoGradoGeneradoEvent(
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion()));

        logger.info(EntregableProyectoGradoKey.LOG_GENERADO,
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion());

        return entregable.getId();
    }
}
