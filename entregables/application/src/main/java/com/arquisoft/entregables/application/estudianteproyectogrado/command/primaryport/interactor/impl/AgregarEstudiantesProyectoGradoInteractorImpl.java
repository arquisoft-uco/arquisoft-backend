package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.interactor.AgregarEstudiantesProyectoGradoInteractor;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.mapper.AgregarEstudiantesProyectoGradoMapper;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase.AgregarEstudiantesProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEstudiantesProyectoGradoInteractorImpl implements AgregarEstudiantesProyectoGradoInteractor {

    private final AgregarEstudiantesProyectoGradoUseCase agregarEstudiantesProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public AgregacionEstudiantesProyectoGradoResult ejecutar(AgregarEstudiantesProyectoGradoCommand command) {
        return agregarEstudiantesProyectoGradoUseCase.ejecutar(
                AgregarEstudiantesProyectoGradoMapper.toDomain(command));
    }
}
