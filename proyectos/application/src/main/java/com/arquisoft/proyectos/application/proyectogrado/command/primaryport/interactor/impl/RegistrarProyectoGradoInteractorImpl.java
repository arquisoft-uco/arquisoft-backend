package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.RegistrarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper.RegistrarProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.RegistrarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarProyectoGradoInteractorImpl implements RegistrarProyectoGradoInteractor {

    private final RegistrarProyectoGradoUseCase registrarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public RegistroProyectoGradoResult ejecutar(RegistrarProyectoGradoCommand command) {
        return registrarProyectoGradoUseCase.ejecutar(RegistrarProyectoGradoMapper.toDomain(command));
    }
}
