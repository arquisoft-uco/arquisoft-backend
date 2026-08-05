package com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.RemoverEstudianteFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.RemoverEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilUseCaseImpl implements RemoverEstudianteFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(RemoverEstudianteFichaPerfilDomain entrada) {
        UUID fichaPerfil = entrada.getFichaPerfil();
        UUID estudiante = entrada.getEstudiante();

        removerEstudianteFichaPerfilValidator.validar(fichaPerfil, estudiante);

        estudianteFichaPerfilOutputPort.desvincularEstudiante(fichaPerfil, estudiante);

        logger.info(catalog.obtener(FichasKeys.EstudianteFichaPerfil.LOG_REMOVIDO), fichaPerfil, estudiante);
    }
}
