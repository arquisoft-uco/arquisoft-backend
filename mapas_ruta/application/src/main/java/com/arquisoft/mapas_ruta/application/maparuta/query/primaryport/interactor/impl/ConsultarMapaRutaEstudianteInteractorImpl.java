package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapaRutaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper.ConsultarMapaRutaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapaRutaEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarMapaRutaEstudianteInteractorImpl implements ConsultarMapaRutaEstudianteInteractor {

    private final ConsultarMapaRutaEstudianteUseCase consultarMapaRutaEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public Optional<MapaRutaEstudianteReadModel> ejecutar(ConsultarMapaRutaEstudianteQuery entrada) {
        var criteria = ConsultarMapaRutaEstudianteMapper.toCriteria(entrada);
        return consultarMapaRutaEstudianteUseCase.ejecutar(criteria);
    }
}
