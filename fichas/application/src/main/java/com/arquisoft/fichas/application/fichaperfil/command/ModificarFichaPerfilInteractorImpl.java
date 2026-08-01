package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilInteractorImpl implements ModificarFichaPerfilInteractor {

    private final ModificarFichaPerfilUseCase modificarFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarFichaPerfilCommand command) {
        modificarFichaPerfilUseCase.ejecutar(command);
    }
}
