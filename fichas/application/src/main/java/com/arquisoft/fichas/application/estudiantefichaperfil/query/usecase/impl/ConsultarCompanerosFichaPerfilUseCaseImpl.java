package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarCompanerosFichaPerfilUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCompanerosFichaPerfilUseCaseImpl implements ConsultarCompanerosFichaPerfilUseCase {

    private final EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstudianteFichaPerfilReadModel> ejecutar(EstudianteFichaPerfilCompaneroCriteria entrada) {
        logger.debug(EstudianteFichaPerfilKey.LOG_CONSULTANDO_COMPANEROS, entrada.fichaPerfil(), entrada.estudiante());

        var companeros = estudianteFichaPerfilQueryOutputPort
                .consultarCompanerosPorFichaYEstudiante(entrada.fichaPerfil(), entrada.estudiante());

        logger.debug(EstudianteFichaPerfilKey.LOG_CONSULTA_COMPANEROS_COMPLETADA, companeros.size());
        return companeros;
    }
}
