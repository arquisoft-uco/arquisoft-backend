package com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.RemoverAutorInteractor;
import com.arquisoft.biblioteca.application.autor.command.primaryport.mapper.RemoverAutorMapper;
import com.arquisoft.biblioteca.application.autor.command.primaryport.model.RemoverAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.usecase.RemoverAutorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAutorInteractorImpl implements RemoverAutorInteractor {

    private final RemoverAutorUseCase removerAutorUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public RemocionAutorResult ejecutar(RemoverAutorCommand command) {
        return removerAutorUseCase.ejecutar(RemoverAutorMapper.toDomain(command));
    }
}
