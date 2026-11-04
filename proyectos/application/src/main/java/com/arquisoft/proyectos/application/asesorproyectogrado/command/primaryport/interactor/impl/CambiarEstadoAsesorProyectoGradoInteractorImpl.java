package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.CambiarEstadoAsesorProyectoGradoInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper.CambiarEstadoAsesorProyectoGradoMapper;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase.CambiarEstadoAsesorProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoAsesorProyectoGradoInteractorImpl implements CambiarEstadoAsesorProyectoGradoInteractor {

    private final CambiarEstadoAsesorProyectoGradoUseCase cambiarEstadoAsesorProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public void ejecutar(CambiarEstadoAsesorProyectoGradoCommand command) {
        cambiarEstadoAsesorProyectoGradoUseCase.ejecutar(CambiarEstadoAsesorProyectoGradoMapper.toDomain(command));
    }
}
