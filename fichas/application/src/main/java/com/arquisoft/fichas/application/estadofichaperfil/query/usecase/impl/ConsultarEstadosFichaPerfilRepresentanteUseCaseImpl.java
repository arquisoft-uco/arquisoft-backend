package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilRepresentanteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilRepresentanteUseCaseImpl implements ConsultarEstadosFichaPerfilRepresentanteUseCase {

    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoFichaPerfilReadModel> ejecutar(EstadoFichaPerfilRepresentanteCriteria entrada) {
        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE, entrada.fichaPerfil());

        var estados = estadoFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(
                entrada.fichaPerfil(), entrada.representanteComite());

        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA, estados.size());
        return estados;
    }
}
