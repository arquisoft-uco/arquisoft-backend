package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.ModificarInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper.ModificarInformacionTareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.ModificarInformacionTareaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarInformacionTareaInteractorImpl implements ModificarInformacionTareaInteractor {

    private final ModificarInformacionTareaUseCase modificarInformacionTareaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(ModificarInformacionTareaCommand command) {
        var modificacion = ModificarInformacionTareaMapper.toDomain(command);

        modificarInformacionTareaUseCase.ejecutar(modificacion);
    }
}
