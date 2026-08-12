package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilUseCaseImpl implements RemoverEstudianteFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final EstudiantesExistentesFinder estudiantesExistentesFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(RemocionEstudianteFichaPerfilDomain entrada) {
        UUID fichaPerfil = entrada.getFichaPerfil();
        UUID estudiante = entrada.getEstudiante();

        boolean fichaExiste = fichaPerfilExisteFinder.obtener(fichaPerfil);
        List<UUID> estudiantesExistentes = estudiantesExistentesFinder.obtener(List.of(estudiante));
        boolean vinculoExiste = vinculoEstudianteFichaExisteFinder.obtener(
                new VinculoEstudianteFicha(fichaPerfil, estudiante));

        removerEstudianteFichaPerfilValidator.validar(
                entrada, fichaExiste, estudiantesExistentes, vinculoExiste);

        estudianteFichaPerfilOutputPort.desvincularEstudiante(fichaPerfil, estudiante);

        logger.info(catalogo.obtener(EstudianteFichaPerfilKey.LOG_REMOVIDO), fichaPerfil, estudiante);
    }
}
