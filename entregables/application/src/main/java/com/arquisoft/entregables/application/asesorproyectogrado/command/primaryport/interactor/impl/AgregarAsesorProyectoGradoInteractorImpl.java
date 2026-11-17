package com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.interactor.AgregarAsesorProyectoGradoInteractor;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.mapper.AgregarAsesorProyectoGradoMapper;
import com.arquisoft.entregables.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.AgregarAsesorProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorProyectoGradoInteractorImpl implements AgregarAsesorProyectoGradoInteractor {

    private final AgregarAsesorProyectoGradoUseCase agregarAsesorProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public AgregacionAsesorProyectoGradoResult ejecutar(AgregarAsesorProyectoGradoCommand command) {
        return agregarAsesorProyectoGradoUseCase.ejecutar(AgregarAsesorProyectoGradoMapper.toDomain(command));
    }
}
