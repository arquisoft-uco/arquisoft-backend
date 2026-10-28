package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.RemoverInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper.RemoverInformacionTareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.RemoverInformacionTareaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverInformacionTareaInteractorImpl implements RemoverInformacionTareaInteractor {

    private final RemoverInformacionTareaUseCase removerInformacionTareaUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public void ejecutar(RemoverInformacionTareaCommand command) {
        var remocion = RemoverInformacionTareaMapper.toDomain(command);

        removerInformacionTareaUseCase.ejecutar(remocion);
    }
}
