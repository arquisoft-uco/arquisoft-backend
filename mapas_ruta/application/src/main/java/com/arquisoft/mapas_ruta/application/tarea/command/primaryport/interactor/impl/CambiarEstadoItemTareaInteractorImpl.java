package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.CambiarEstadoItemTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper.CambiarEstadoItemTareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.CambiarEstadoItemTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.CambiarEstadoItemTareaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoItemTareaInteractorImpl implements CambiarEstadoItemTareaInteractor {

    private final CambiarEstadoItemTareaUseCase cambiarEstadoItemTareaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(CambiarEstadoItemTareaCommand command) {
        var cambio = CambiarEstadoItemTareaMapper.toDomain(command);

        cambiarEstadoItemTareaUseCase.ejecutar(cambio);
    }
}
