package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.AtrasarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper.AtrasarProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.AtrasarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.AtrasoProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.AtrasarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AtrasarProyectoGradoInteractorImpl implements AtrasarProyectoGradoInteractor {

    private final AtrasarProyectoGradoUseCase atrasarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AtrasoProyectoGradoResult ejecutar(AtrasarProyectoGradoCommand command) {
        return atrasarProyectoGradoUseCase.ejecutar(AtrasarProyectoGradoMapper.toDomain(command));
    }
}
