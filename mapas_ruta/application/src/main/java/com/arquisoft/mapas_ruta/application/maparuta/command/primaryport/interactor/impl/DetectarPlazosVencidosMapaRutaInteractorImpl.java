package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.DetectarPlazosVencidosMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper.DetectarPlazosVencidosMapaRutaMapper;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.DetectarPlazosVencidosMapaRutaCommand;
import com.arquisoft.mapas_ruta.application.maparuta.command.usecase.DetectarPlazosVencidosMapaRutaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DetectarPlazosVencidosMapaRutaInteractorImpl implements DetectarPlazosVencidosMapaRutaInteractor {

    private final DetectarPlazosVencidosMapaRutaUseCase detectarPlazosVencidosMapaRutaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(DetectarPlazosVencidosMapaRutaCommand command) {
        detectarPlazosVencidosMapaRutaUseCase.ejecutar(DetectarPlazosVencidosMapaRutaMapper.toDomain(command));
    }
}
