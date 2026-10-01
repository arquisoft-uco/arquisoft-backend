package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.AgregarAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.mapper.AgregarAsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.AgregarAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorInteractorImpl implements AgregarAsesorInteractor {

    private final AgregarAsesorUseCase agregarAsesorUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionAsesorResult ejecutar(AgregarAsesorCommand command) {
        return agregarAsesorUseCase.ejecutar(AgregarAsesorMapper.toDomain(command));
    }
}
