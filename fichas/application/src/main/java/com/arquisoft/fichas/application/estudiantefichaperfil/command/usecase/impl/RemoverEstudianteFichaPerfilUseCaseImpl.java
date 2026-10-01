package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantesExistentesFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.RemoverEstudianteFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilUseCaseImpl implements RemoverEstudianteFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;
    private final EstudiantesExistentesFinder estudiantesExistentesFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionEstudianteFichaPerfilDomain entrada) {
        var fichaPerfil = entrada.getFichaPerfil();
        var estudiante = entrada.getEstudiante();

        logger.info(EstudianteFichaPerfilKey.LOG_REMOVIENDO, fichaPerfil, estudiante);

        var fichaExiste = fichaPerfilExisteFinder.obtener(fichaPerfil);
        var estadoActual = estadoActualFichaPerfilFinder.obtener(fichaPerfil);
        var estudiantesExistentes = estudiantesExistentesFinder.obtener(List.of(estudiante));
        var vinculoExiste = vinculoEstudianteFichaExisteFinder.obtener(
                new VinculoEstudianteFicha(fichaPerfil, estudiante));

        logger.debug(EstudianteFichaPerfilKey.LOG_VERIFICACION_REMOVER,
                fichaExiste, !estudiantesExistentes.isEmpty(), vinculoExiste);

        removerEstudianteFichaPerfilValidator.validar(
                entrada, fichaExiste, estadoActual, estudiantesExistentes, vinculoExiste);

        estudianteFichaPerfilOutputPort.desvincularEstudiante(fichaPerfil, estudiante);

        logger.info(EstudianteFichaPerfilKey.LOG_REMOVIDO, fichaPerfil, estudiante);
    }
}
