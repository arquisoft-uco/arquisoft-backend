package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.ReanudarProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper.ReanudarProyectoGradoMapper;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.ReanudarProyectoGradoCommand;
import com.arquisoft.proyectos.application.proyectogrado.command.result.ReanudacionProyectoGradoResult;
import com.arquisoft.proyectos.application.proyectogrado.command.usecase.ReanudarProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReanudarProyectoGradoInteractorImpl implements ReanudarProyectoGradoInteractor {

    private final ReanudarProyectoGradoUseCase reanudarProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public ReanudacionProyectoGradoResult ejecutar(ReanudarProyectoGradoCommand command) {
        return reanudarProyectoGradoUseCase.ejecutar(ReanudarProyectoGradoMapper.toDomain(command));
    }
}
