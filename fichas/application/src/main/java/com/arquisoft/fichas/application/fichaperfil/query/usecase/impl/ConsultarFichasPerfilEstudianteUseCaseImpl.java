package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilEstudianteQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilEstudianteUseCaseImpl implements ConsultarFichasPerfilEstudianteUseCase {

    private final FichaPerfilEstudianteQueryOutputPort fichaPerfilEstudianteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<FichaPerfilEstudianteReadModel> ejecutar(FichaPerfilEstudianteCriteria criteria) {
        logger.debug(FichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, criteria.estudiante());

        var resultado = fichaPerfilEstudianteQueryOutputPort.consultarPorEstudiante(criteria);

        logger.debug(FichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, resultado.size());

        return resultado;
    }
}
