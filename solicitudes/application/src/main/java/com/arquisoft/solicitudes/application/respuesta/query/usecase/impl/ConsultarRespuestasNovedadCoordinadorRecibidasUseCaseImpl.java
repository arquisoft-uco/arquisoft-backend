package com.arquisoft.solicitudes.application.respuesta.query.usecase.impl;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.secondaryport.RespuestaQueryOutputPort;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadCoordinadorRecibidasUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasNovedadCoordinadorRecibidasUseCaseImpl
        implements ConsultarRespuestasNovedadCoordinadorRecibidasUseCase {

    private final RespuestaQueryOutputPort respuestaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RespuestaReadModel> ejecutar(RespuestaCriteria entrada) {
        logger.debug(RespuestaKey.LOG_CONSULTANDO_NOVEDAD_COORDINADOR_RECIBIDAS,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = respuestaQueryOutputPort.consultar(entrada);

        logger.debug(RespuestaKey.LOG_CONSULTA_NOVEDAD_COORDINADOR_RECIBIDAS_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
