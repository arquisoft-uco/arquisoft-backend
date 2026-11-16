package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.interactor.RemoverEstudianteProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.mapper.RemoverEstudianteProyectoGradoMapper;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.RemoverEstudianteProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteProyectoGradoInteractorImpl implements RemoverEstudianteProyectoGradoInteractor {

    private final RemoverEstudianteProyectoGradoUseCase removerEstudianteProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public RemocionEstudianteProyectoGradoResult ejecutar(RemoverEstudianteProyectoGradoCommand command) {
        return removerEstudianteProyectoGradoUseCase.ejecutar(
                RemoverEstudianteProyectoGradoMapper.toDomain(command));
    }
}
