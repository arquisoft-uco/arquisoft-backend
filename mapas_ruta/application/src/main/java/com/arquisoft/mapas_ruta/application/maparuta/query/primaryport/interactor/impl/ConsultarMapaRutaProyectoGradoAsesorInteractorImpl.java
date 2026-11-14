package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapaRutaProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper.ConsultarMapaRutaProyectoGradoAsesorMapper;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapaRutaProyectoGradoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarMapaRutaProyectoGradoAsesorInteractorImpl
        implements ConsultarMapaRutaProyectoGradoAsesorInteractor {

    private final ConsultarMapaRutaProyectoGradoAsesorUseCase consultarMapaRutaProyectoGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public Optional<MapaRutaEstudianteReadModel> ejecutar(ConsultarMapaRutaProyectoGradoAsesorQuery entrada) {
        var criteria = ConsultarMapaRutaProyectoGradoAsesorMapper.toCriteria(entrada);
        return consultarMapaRutaProyectoGradoAsesorUseCase.ejecutar(criteria);
    }
}
