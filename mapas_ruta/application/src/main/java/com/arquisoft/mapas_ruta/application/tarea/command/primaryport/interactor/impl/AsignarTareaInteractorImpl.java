package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.AsignarTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper.AsignarTareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.AsignarTareaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarTareaInteractorImpl implements AsignarTareaInteractor {

    private final AsignarTareaUseCase asignarTareaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public UUID ejecutar(AsignarTareaCommand command) {
        var tarea = AsignarTareaMapper.toDomain(command);

        return asignarTareaUseCase.ejecutar(tarea);
    }
}
