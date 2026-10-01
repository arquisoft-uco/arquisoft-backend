package com.arquisoft.seguridad.application.auth.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.CerrarSesionInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.mapper.CerrarSesionMapper;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.CerrarSesionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CerrarSesionInteractorImpl implements CerrarSesionInteractor {

    private final CerrarSesionUseCase cerrarSesionUseCase;

    @Override
    public void ejecutar(TokenSesionCommand entrada) {
        cerrarSesionUseCase.ejecutar(CerrarSesionMapper.toDomain(entrada));
    }
}
