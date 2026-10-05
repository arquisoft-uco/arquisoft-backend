package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaQueryOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.query.usecase.ConsultarMapasRutaCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarMapasRutaCoordinadorUseCaseImpl implements ConsultarMapasRutaCoordinadorUseCase {

    private final MapaRutaQueryOutputPort mapaRutaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<MapaRutaReadModel> ejecutar(MapaRutaCriteria entrada) {
        logger.debug(MapaRutaKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = mapaRutaQueryOutputPort.consultarTodos(entrada);

        logger.debug(MapaRutaKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
