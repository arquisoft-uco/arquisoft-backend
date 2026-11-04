package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilCoordinadorUseCaseImpl implements ConsultarEstadosFichaPerfilCoordinadorUseCase {

    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoFichaPerfilReadModel> ejecutar(EstadoFichaPerfilCoordinadorCriteria entrada) {
        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTANDO_COORDINADOR, entrada.fichaPerfil());

        var estados = estadoFichaPerfilQueryOutputPort.consultarPorFicha(entrada.fichaPerfil());

        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA, estados.size());
        return estados;
    }
}
