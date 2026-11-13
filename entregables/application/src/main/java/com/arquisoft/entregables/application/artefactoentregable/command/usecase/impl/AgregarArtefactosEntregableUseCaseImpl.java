package com.arquisoft.entregables.application.artefactoentregable.command.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.finder.ArtefactosReplicadosFinder;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.mapper.ArtefactoEntregableMapper;
import com.arquisoft.entregables.application.artefactoentregable.command.usecase.AgregarArtefactosEntregableUseCase;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarArtefactosEntregableUseCaseImpl implements AgregarArtefactosEntregableUseCase {

    private final ArtefactoEntregableOutputPort artefactoEntregableOutputPort;
    private final ArtefactosReplicadosFinder artefactosReplicadosFinder;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionArtefactosEntregableDomain agregacion) {
        if (UtilColeccion.esVaciaONula(agregacion.getArtefactos())) {
            return;
        }

        var versiones = agregacion.getArtefactos().stream()
                .map(ArtefactoEntregableDomain::getVersionArtefacto)
                .toList();
        var replicados = artefactosReplicadosFinder.obtener(versiones);
        logger.debug(ArtefactoEntregableKey.LOG_VINCULADOS,
                agregacion.getEntregableProyectoGrado(), agregacion.getArtefactos().size(), replicados.size());

        var entidades = agregacion.getArtefactos().stream()
                .map(ArtefactoEntregableMapper::toEntity)
                .toList();
        var faltantes = entidades.stream()
                .filter(entidad -> !replicados.contains(entidad.versionArtefacto()))
                .toList();

        artefactoEntregableOutputPort.guardarArtefactos(faltantes);
        artefactoEntregableOutputPort.vincular(entidades);
    }
}
