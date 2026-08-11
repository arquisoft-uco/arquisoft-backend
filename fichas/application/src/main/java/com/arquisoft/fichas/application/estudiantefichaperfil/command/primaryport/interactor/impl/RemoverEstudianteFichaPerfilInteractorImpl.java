package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.RemoverEstudianteFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper.RemoverEstudianteFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.RemoverEstudianteFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilInteractorImpl implements RemoverEstudianteFichaPerfilInteractor {

    private final RemoverEstudianteFichaPerfilUseCase removerEstudianteFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverEstudianteFichaPerfilCommand command) {
        removerEstudianteFichaPerfilUseCase.ejecutar(RemoverEstudianteFichaPerfilMapper.toDomain(command));
    }
}
