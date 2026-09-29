package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.AgregarMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper.AgregarMapaRutaMapper;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.AgregarMapaRutaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarMapaRutaInteractorImpl implements AgregarMapaRutaInteractor {

    private final AgregarMapaRutaUseCase agregarMapaRutaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public UUID ejecutar(AgregarMapaRutaCommand command) {
        var agregacion = AgregarMapaRutaMapper.toDomain(command);

        return agregarMapaRutaUseCase.ejecutar(agregacion);
    }
}
