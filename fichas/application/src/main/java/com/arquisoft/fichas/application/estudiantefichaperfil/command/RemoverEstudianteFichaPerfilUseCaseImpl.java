package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilUseCaseImpl implements RemoverEstudianteFichaPerfilUseCase {

    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final RemoverEstudianteFichaPerfilValidator removerEstudianteFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemoverEstudianteFichaPerfilCommand entrada) {
        UUID fichaPerfil = entrada.fichaPerfil();
        UUID estudiante = entrada.estudiante();

        removerEstudianteFichaPerfilValidator.validar(fichaPerfil, estudiante);

        estudianteFichaPerfilOutputPort.eliminar(fichaPerfil, estudiante);

        logger.info(FichasMessages.EstudianteFichaPerfil.LOG_REMOVIDO, fichaPerfil, estudiante);
    }
}
