package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper.RegistrarResultadoEvaluacionEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarResultadoEvaluacionEntregableProyectoGradoInteractorImpl
        implements RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor {

    private final RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase
            registrarResultadoEvaluacionEntregableProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public RegistroResultadoEvaluacionEntregableProyectoGradoResult ejecutar(
            RegistrarResultadoEvaluacionEntregableProyectoGradoCommand command) {
        return registrarResultadoEvaluacionEntregableProyectoGradoUseCase.ejecutar(
                RegistrarResultadoEvaluacionEntregableProyectoGradoMapper.toDomain(command));
    }
}
