package com.arquisoft.fichas.application.fichaperfil.command.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.CambiarAsesorFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaInteractorImpl implements CambiarAsesorFichaInteractor {

    private final CambiarAsesorFichaUseCase cambiarAsesorFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(CambiarAsesorFichaCommand command) {
        cambiarAsesorFichaUseCase.ejecutar(command);
    }
}
