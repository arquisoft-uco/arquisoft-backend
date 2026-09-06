package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilEstudianteQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichaPerfilEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarFichaPerfilEstudianteUseCaseImpl implements ConsultarFichaPerfilEstudianteUseCase {

    private final FichaPerfilEstudianteQueryOutputPort fichaPerfilEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public Optional<FichaPerfilEstudianteReadModel> ejecutar(FichaPerfilEstudianteCriteria criteria) {
        logger.debug(FichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, criteria.fichaPerfil(), criteria.estudiante());

        var resultado = fichaPerfilEstudianteQueryOutputPort.consultar(criteria);

        logger.debug(FichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, resultado.isPresent());

        return resultado;
    }
}
