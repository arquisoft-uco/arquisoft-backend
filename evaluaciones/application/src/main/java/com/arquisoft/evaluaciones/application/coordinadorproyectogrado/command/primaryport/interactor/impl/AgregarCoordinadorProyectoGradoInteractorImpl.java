package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.interactor.AgregarCoordinadorProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.mapper.AgregarCoordinadorProyectoGradoMapper;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase.AgregarCoordinadorProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorProyectoGradoInteractorImpl implements AgregarCoordinadorProyectoGradoInteractor {

    private final AgregarCoordinadorProyectoGradoUseCase agregarCoordinadorProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public AgregacionCoordinadorProyectoGradoResult ejecutar(AgregarCoordinadorProyectoGradoCommand command) {
        return agregarCoordinadorProyectoGradoUseCase.ejecutar(
                AgregarCoordinadorProyectoGradoMapper.toDomain(command));
    }
}
