package com.arquisoft.fichas.application.fichaperfil.command.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.interactor.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.mapper.ModificarFichaPerfilMapper;
import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.ModificarFichaPerfilUseCase;
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
        modificarFichaPerfilUseCase.ejecutar(ModificarFichaPerfilMapper.toDomain(command));
    }
}
