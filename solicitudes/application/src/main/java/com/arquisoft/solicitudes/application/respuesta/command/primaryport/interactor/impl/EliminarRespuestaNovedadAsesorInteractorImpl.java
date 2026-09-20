package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.EliminarRespuestaNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaNovedadAsesorInteractorImpl
        implements EliminarRespuestaNovedadAsesorInteractor {

    private final EliminarRespuestaNovedadAsesorUseCase eliminarRespuestaNovedadAsesorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarRespuestaNovedadAsesorCommand command) {
        eliminarRespuestaNovedadAsesorUseCase.ejecutar(
                EliminarRespuestaNovedadAsesorMapper.toDomain(command));
    }
}
