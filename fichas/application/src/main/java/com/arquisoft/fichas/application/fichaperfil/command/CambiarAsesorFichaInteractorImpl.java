package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.CambiarAsesorFichaUseCase;
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
