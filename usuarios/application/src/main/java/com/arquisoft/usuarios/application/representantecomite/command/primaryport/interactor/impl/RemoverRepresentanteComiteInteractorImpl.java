package com.arquisoft.usuarios.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.representantecomite.command.primaryport.interactor.RemoverRepresentanteComiteInteractor;
import com.arquisoft.usuarios.application.representantecomite.command.primaryport.mapper.RemoverRepresentanteComiteMapper;
import com.arquisoft.usuarios.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.usuarios.application.representantecomite.command.usecase.RemoverRepresentanteComiteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverRepresentanteComiteInteractorImpl implements RemoverRepresentanteComiteInteractor {

    private final RemoverRepresentanteComiteUseCase removerRepresentanteComiteUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverRepresentanteComiteCommand command) {
        removerRepresentanteComiteUseCase.ejecutar(RemoverRepresentanteComiteMapper.toDomain(command));
    }
}
