package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.AgregarAsesorProyectosInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.mapper.AgregarAsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.AgregarAsesorProyectosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorProyectosInteractorImpl implements AgregarAsesorProyectosInteractor {

    private final AgregarAsesorProyectosUseCase agregarAsesorProyectosUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionAsesorResult ejecutar(AgregarAsesorCommand command) {
        return agregarAsesorProyectosUseCase.ejecutar(AgregarAsesorMapper.toDomain(command));
    }
}
