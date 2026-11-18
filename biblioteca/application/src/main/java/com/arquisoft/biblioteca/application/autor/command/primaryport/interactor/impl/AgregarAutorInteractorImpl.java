package com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.AgregarAutorInteractor;
import com.arquisoft.biblioteca.application.autor.command.primaryport.mapper.AgregarAutorMapper;
import com.arquisoft.biblioteca.application.autor.command.primaryport.model.AgregarAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.usecase.AgregarAutorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAutorInteractorImpl implements AgregarAutorInteractor {

    private final AgregarAutorUseCase agregarAutorUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public AgregacionAutorResult ejecutar(AgregarAutorCommand command) {
        return agregarAutorUseCase.ejecutar(AgregarAutorMapper.toDomain(command));
    }
}
