package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.DestituirEstudianteProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper.DestituirEstudianteProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.DestituirEstudianteProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DestituirEstudianteProyectoGradoInteractorImpl implements DestituirEstudianteProyectoGradoInteractor {

    private final DestituirEstudianteProyectoGradoUseCase destituirEstudianteProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public void ejecutar(DestituirEstudianteProyectoGradoCommand command) {
        destituirEstudianteProyectoGradoUseCase.ejecutar(DestituirEstudianteProyectoGradoMapper.toDomain(command));
    }
}
