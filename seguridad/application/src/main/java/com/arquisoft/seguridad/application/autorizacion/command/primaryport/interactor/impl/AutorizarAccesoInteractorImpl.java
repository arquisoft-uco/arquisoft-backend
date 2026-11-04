package com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.mapper.AutorizarAccesoMapper;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.usecase.AutorizarAccesoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutorizarAccesoInteractorImpl implements AutorizarAccesoInteractor {

    private final AutorizarAccesoUseCase autorizarAccesoUseCase;

    @Override
    public DecisionAccesoResult ejecutar(AutorizarAccesoCommand entrada) {
        return autorizarAccesoUseCase.ejecutar(AutorizarAccesoMapper.toDomain(entrada));
    }
}
