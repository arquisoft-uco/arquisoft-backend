package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapasRutaCoordinadorInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper.ConsultarMapasRutaCoordinadorMapper;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapasRutaCoordinadorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarMapasRutaCoordinadorInteractorImpl implements ConsultarMapasRutaCoordinadorInteractor {

    private final ConsultarMapasRutaCoordinadorUseCase consultarMapasRutaCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<MapaRutaReadModel> ejecutar(ConsultarMapasRutaCoordinadorQuery entrada) {
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(entrada);
        return consultarMapasRutaCoordinadorUseCase.ejecutar(criteria);
    }
}
