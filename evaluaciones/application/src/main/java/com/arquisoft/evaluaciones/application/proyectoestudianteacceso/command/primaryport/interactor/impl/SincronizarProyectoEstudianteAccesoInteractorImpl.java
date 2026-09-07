package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.interactor.SincronizarProyectoEstudianteAccesoInteractor;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.mapper.SincronizarProyectoEstudianteAccesoMapper;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.usecase.SincronizarProyectoEstudianteAccesoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SincronizarProyectoEstudianteAccesoInteractorImpl
        implements SincronizarProyectoEstudianteAccesoInteractor {

    private final SincronizarProyectoEstudianteAccesoUseCase sincronizarProyectoEstudianteAccesoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(SincronizarProyectoEstudianteAccesoCommand entrada) {
        sincronizarProyectoEstudianteAccesoUseCase.ejecutar(
                SincronizarProyectoEstudianteAccesoMapper.toDomain(entrada));
    }
}
