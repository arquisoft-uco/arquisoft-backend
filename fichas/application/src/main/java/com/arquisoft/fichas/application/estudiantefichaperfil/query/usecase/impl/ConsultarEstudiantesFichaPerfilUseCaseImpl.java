package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.ConsultarEstudiantesFichaPerfilUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesFichaPerfilUseCaseImpl implements ConsultarEstudiantesFichaPerfilUseCase {

    private final EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstudianteFichaPerfilReadModel> ejecutar(EstudianteFichaPerfilCriteria entrada) {
        logger.debug(EstudianteFichaPerfilKey.LOG_CONSULTANDO, entrada.fichaPerfil());

        var estudiantes = estudianteFichaPerfilQueryOutputPort.consultarPorFicha(entrada.fichaPerfil());

        logger.debug(EstudianteFichaPerfilKey.LOG_CONSULTA_COMPLETADA, estudiantes.size());
        return estudiantes;
    }
}
