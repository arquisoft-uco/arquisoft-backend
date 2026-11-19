package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.AgregarProyectoGradoInteractor;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.mapper.AgregarProyectoGradoMapper;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoInteractorImpl implements AgregarProyectoGradoInteractor {

    private final AgregarProyectoGradoUseCase agregarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "mapasRutaTransactionManager")
    public AgregacionProyectoGradoResult ejecutar(AgregarProyectoGradoCommand command) {
        return agregarProyectoGradoUseCase.ejecutar(AgregarProyectoGradoMapper.toDomain(command));
    }
}
