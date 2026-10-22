package com.arquisoft.mapas_ruta.application.tarea.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarTareasMapaRutaEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarTareasMapaRutaEstudianteUseCaseImpl implements ConsultarTareasMapaRutaEstudianteUseCase {

    private final TareaQueryOutputPort tareaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<TareaReadModel> ejecutar(TareaMapaRutaCriteria entrada) {
        logger.debug(TareaKey.LOG_CONSULTANDO_POR_MAPA_RUTA,
                entrada.getMapaRuta(), entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = tareaQueryOutputPort.consultarPorMapaRuta(entrada);

        logger.debug(TareaKey.LOG_CONSULTA_POR_MAPA_RUTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
