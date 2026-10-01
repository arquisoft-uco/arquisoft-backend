package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilEstudianteUseCaseImpl implements ConsultarEstadosFichaPerfilEstudianteUseCase {

    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoFichaPerfilReadModel> ejecutar(EstadoFichaPerfilEstudianteCriteria entrada) {
        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, entrada.fichaPerfil());

        var estados = estadoFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(
                entrada.fichaPerfil(), entrada.estudiante());

        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, estados.size());
        return estados;
    }
}
