package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.interactor.SincronizarEntregableProyectoAccesoInteractor;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.mapper.SincronizarEntregableProyectoAccesoMapper;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.usecase.SincronizarEntregableProyectoAccesoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SincronizarEntregableProyectoAccesoInteractorImpl
        implements SincronizarEntregableProyectoAccesoInteractor {

    private final SincronizarEntregableProyectoAccesoUseCase sincronizarEntregableProyectoAccesoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(SincronizarEntregableProyectoAccesoCommand entrada) {
        sincronizarEntregableProyectoAccesoUseCase.ejecutar(
                SincronizarEntregableProyectoAccesoMapper.toDomain(entrada));
    }
}
