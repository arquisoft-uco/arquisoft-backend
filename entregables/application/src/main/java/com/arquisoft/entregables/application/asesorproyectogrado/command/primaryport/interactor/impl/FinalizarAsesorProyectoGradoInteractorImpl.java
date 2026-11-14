package com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.FinalizarAsesorProyectoGradoInteractor;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.mapper.FinalizarAsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model.FinalizarAsesorProyectoGradoCommand;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.FinalizarAsesorProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FinalizarAsesorProyectoGradoInteractorImpl implements FinalizarAsesorProyectoGradoInteractor {

    private final FinalizarAsesorProyectoGradoUseCase finalizarAsesorProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public FinalizacionAsesorProyectoGradoResult ejecutar(FinalizarAsesorProyectoGradoCommand command) {
        return finalizarAsesorProyectoGradoUseCase.ejecutar(FinalizarAsesorProyectoGradoMapper.toDomain(command));
    }
}
