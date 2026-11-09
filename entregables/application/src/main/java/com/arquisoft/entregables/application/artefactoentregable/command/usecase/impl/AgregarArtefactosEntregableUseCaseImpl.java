package com.arquisoft.entregables.application.artefactoentregable.command.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.mapper.ArtefactoEntregableMapper;
import com.arquisoft.entregables.application.artefactoentregable.command.usecase.AgregarArtefactosEntregableUseCase;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarArtefactosEntregableUseCaseImpl implements AgregarArtefactosEntregableUseCase {

    private final ArtefactoEntregableOutputPort artefactoEntregableOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionArtefactosEntregableDomain agregacion) {
        var cantidad = agregacion.getArtefactos().size();
        logger.debug(ArtefactoEntregableKey.LOG_VINCULADOS, agregacion.getEntregableProyectoGrado(), cantidad);

        if (UtilColeccion.esVaciaONula(agregacion.getArtefactos())) {
            return;
        }

        artefactoEntregableOutputPort.guardarTodos(agregacion.getArtefactos().stream()
                .map(ArtefactoEntregableMapper::toEntity)
                .toList());
    }
}
