package com.arquisoft.fichas.application.evaluacionfichaperfil.command;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in.RegistrarEvaluacionFichaPerfilInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionFichaPerfilInteractor implements RegistrarEvaluacionFichaPerfilInputPort {

    private final RegistrarEvaluacionFichaPerfilUseCase registrarEvaluacionFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand command) {
        return registrarEvaluacionFichaPerfilUseCase.ejecutar(command);
    }
}
