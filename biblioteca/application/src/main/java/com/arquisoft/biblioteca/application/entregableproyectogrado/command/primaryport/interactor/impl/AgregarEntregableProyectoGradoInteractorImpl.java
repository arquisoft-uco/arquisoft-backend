package com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.interactor.AgregarEntregableProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.mapper.AgregarEntregableProyectoGradoMapper;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model.AgregarEntregableProyectoGradoCommand;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase.AgregarEntregableProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEntregableProyectoGradoInteractorImpl implements AgregarEntregableProyectoGradoInteractor {

    private final AgregarEntregableProyectoGradoUseCase agregarEntregableProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "bibliotecaTransactionManager")
    public AgregacionEntregableProyectoGradoResult ejecutar(AgregarEntregableProyectoGradoCommand command) {
        return agregarEntregableProyectoGradoUseCase.ejecutar(
                AgregarEntregableProyectoGradoMapper.toDomain(command));
    }
}
