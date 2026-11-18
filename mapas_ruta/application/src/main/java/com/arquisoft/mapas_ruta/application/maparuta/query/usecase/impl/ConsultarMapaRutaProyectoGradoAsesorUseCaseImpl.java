package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaProyectoGradoCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaEstudianteQueryOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapaRutaProyectoGradoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarMapaRutaProyectoGradoAsesorUseCaseImpl implements ConsultarMapaRutaProyectoGradoAsesorUseCase {

    private final MapaRutaEstudianteQueryOutputPort mapaRutaEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public Optional<MapaRutaEstudianteReadModel> ejecutar(MapaRutaProyectoGradoCriteria criteria) {
        logger.debug(MapaRutaKey.LOG_CONSULTANDO_PROYECTO_ASESOR, criteria.proyectoGrado());

        var resultado = mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(criteria.proyectoGrado());

        logger.debug(MapaRutaKey.LOG_CONSULTA_PROYECTO_ASESOR_COMPLETADA, resultado.isPresent());

        return resultado;
    }
}
