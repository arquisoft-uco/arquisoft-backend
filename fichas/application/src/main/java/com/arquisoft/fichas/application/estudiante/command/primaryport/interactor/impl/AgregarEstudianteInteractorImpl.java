package com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.AgregarEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.mapper.AgregarEstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.usecase.AgregarEstudianteFichasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteInteractorImpl implements AgregarEstudianteInteractor {

    private final AgregarEstudianteFichasUseCase agregarEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public AgregacionEstudianteResult ejecutar(AgregarEstudianteCommand command) {
        return agregarEstudianteUseCase.ejecutar(AgregarEstudianteMapper.toDomain(command));
    }
}
