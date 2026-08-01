package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        return registrarFichaPerfilUseCase.ejecutar(command);
    }
}
