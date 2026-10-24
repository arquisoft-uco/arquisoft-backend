package com.arquisoft.seguridad.application.mapeoroles.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.mapeoroles.command.primaryport.interactor.ActualizarMapeoRolesInteractor;
import com.arquisoft.seguridad.application.mapeoroles.command.result.ActualizacionMapeoRolesResult;
import com.arquisoft.seguridad.application.mapeoroles.command.usecase.ActualizarMapeoRolesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarMapeoRolesInteractorImpl implements ActualizarMapeoRolesInteractor {

    private final ActualizarMapeoRolesUseCase actualizarMapeoRolesUseCase;

    @Override
    public ActualizacionMapeoRolesResult ejecutar() {
        return actualizarMapeoRolesUseCase.ejecutar();
    }
}
