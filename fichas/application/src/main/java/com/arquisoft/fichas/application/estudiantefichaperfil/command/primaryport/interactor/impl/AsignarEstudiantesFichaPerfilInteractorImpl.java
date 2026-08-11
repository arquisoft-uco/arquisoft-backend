package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.AsignarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper.AsignarEstudiantesFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilInteractorImpl implements AsignarEstudiantesFichaPerfilInteractor {

    private final AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(AsignarEstudiantesFichaPerfilCommand command) {
        asignarEstudiantesFichaPerfilUseCase.ejecutar(AsignarEstudiantesFichaPerfilMapper.toDomain(command));
    }
}
