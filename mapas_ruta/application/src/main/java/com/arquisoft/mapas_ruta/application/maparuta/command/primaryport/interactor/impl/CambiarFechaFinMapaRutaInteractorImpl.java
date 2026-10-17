package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.CambiarFechaFinMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper.CambiarFechaFinMapaRutaMapper;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.CambiarFechaFinMapaRutaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarFechaFinMapaRutaInteractorImpl implements CambiarFechaFinMapaRutaInteractor {

    private final CambiarFechaFinMapaRutaUseCase cambiarFechaFinMapaRutaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(CambiarFechaFinMapaRutaCommand command) {
        var cambio = CambiarFechaFinMapaRutaMapper.toDomain(command);

        cambiarFechaFinMapaRutaUseCase.ejecutar(cambio);
    }
}
