package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.interactor.DestituirEstudianteProyectoGradoInteractor;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.mapper.DestituirEstudianteProyectoGradoMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DestituirEstudianteProyectoGradoInteractorImpl implements DestituirEstudianteProyectoGradoInteractor {

    private final DestituirEstudianteProyectoGradoUseCase destituirEstudianteProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public DestitucionEstudianteProyectoGradoResult ejecutar(DestituirEstudianteProyectoGradoCommand command) {
        return destituirEstudianteProyectoGradoUseCase.ejecutar(
                DestituirEstudianteProyectoGradoMapper.toDomain(command));
    }
}
