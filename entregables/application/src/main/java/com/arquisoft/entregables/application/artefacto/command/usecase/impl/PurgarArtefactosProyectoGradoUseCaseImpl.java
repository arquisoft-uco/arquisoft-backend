package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ClavesArtefactosProyectoGradoFinder;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.artefacto.command.result.mapper.PurgaArtefactosProyectoGradoResultMapper;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.AlmacenamientoArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurgarArtefactosProyectoGradoUseCaseImpl implements PurgarArtefactosProyectoGradoUseCase {

    private final ClavesArtefactosProyectoGradoFinder clavesArtefactosProyectoGradoFinder;
    private final AlmacenamientoArtefactoOutputPort almacenamientoArtefactoOutputPort;

    @Override
    public PurgaArtefactosProyectoGradoResult ejecutar(PurgaArtefactosProyectoGradoDomain purga) {
        var claves = clavesArtefactosProyectoGradoFinder.obtener(purga.getProyectoGrado());
        claves.forEach(almacenamientoArtefactoOutputPort::eliminar);
        return PurgaArtefactosProyectoGradoResultMapper.toResult(purga, claves.size());
    }
}
