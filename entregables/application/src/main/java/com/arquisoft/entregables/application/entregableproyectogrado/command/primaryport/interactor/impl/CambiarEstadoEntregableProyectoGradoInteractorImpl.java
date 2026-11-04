package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.CambiarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper.CambiarEstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.CambiarEstadoEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoEntregableProyectoGradoInteractorImpl
        implements CambiarEstadoEntregableProyectoGradoInteractor {

    private final CambiarEstadoEntregableProyectoGradoUseCase cambiarEstadoEntregableProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public void ejecutar(CambiarEstadoEntregableProyectoGradoCommand command) {
        cambiarEstadoEntregableProyectoGradoUseCase.ejecutar(
                CambiarEstadoEntregableProyectoGradoMapper.toDomain(command));
    }
}
