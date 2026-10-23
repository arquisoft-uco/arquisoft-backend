package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper.ConsultarInformacionTareaMapper;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarInformacionTareaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarInformacionTareaInteractorImpl implements ConsultarInformacionTareaInteractor {

    private final ConsultarInformacionTareaUseCase consultarInformacionTareaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public Optional<TareaReadModel> ejecutar(ConsultarInformacionTareaQuery entrada) {
        var criteria = ConsultarInformacionTareaMapper.toCriteria(entrada);
        return consultarInformacionTareaUseCase.ejecutar(criteria);
    }
}
