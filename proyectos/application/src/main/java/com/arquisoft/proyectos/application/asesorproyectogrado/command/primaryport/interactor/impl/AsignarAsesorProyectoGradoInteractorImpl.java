package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.AsignarAsesorProyectoGradoInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper.AsignarAsesorProyectoGradoMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.AsignarAsesorProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarAsesorProyectoGradoInteractorImpl implements AsignarAsesorProyectoGradoInteractor {

    private final AsignarAsesorProyectoGradoUseCase asignarAsesorProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public UUID ejecutar(AsignarAsesorProyectoGradoCommand command) {
        return asignarAsesorProyectoGradoUseCase.ejecutar(AsignarAsesorProyectoGradoMapper.toDomain(command));
    }
}
