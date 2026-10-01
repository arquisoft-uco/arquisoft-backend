package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper.ModificarFichaPerfilMapper;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.ModificarFichaPerfilCommand;
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
