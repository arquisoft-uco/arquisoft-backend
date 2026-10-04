package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.finder.ProyectoGradoDeEstudianteQueryFinder;
import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaEstudianteCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaEstudianteQueryOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapaRutaEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarMapaRutaEstudianteUseCaseImpl implements ConsultarMapaRutaEstudianteUseCase {

    private final ProyectoGradoDeEstudianteQueryFinder proyectoGradoDeEstudianteQueryFinder;
    private final MapaRutaEstudianteQueryOutputPort mapaRutaEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public Optional<MapaRutaEstudianteReadModel> ejecutar(MapaRutaEstudianteCriteria criteria) {
        logger.debug(MapaRutaKey.LOG_CONSULTANDO_ESTUDIANTE, criteria.estudiante());

        var proyectoGrado = proyectoGradoDeEstudianteQueryFinder.obtener(criteria.estudiante());
        var proyectoAsignado = !UtilUUID.esPorDefecto(proyectoGrado);

        var resultado = proyectoAsignado
                ? mapaRutaEstudianteQueryOutputPort.consultarPorProyectoGrado(proyectoGrado)
                : Optional.<MapaRutaEstudianteReadModel>empty();

        logger.debug(MapaRutaKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, proyectoAsignado, resultado.isPresent());

        return resultado;
    }
}
