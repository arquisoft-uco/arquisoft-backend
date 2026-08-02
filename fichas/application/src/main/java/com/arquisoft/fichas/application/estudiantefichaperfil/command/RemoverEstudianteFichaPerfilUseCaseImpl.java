package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilUseCaseImpl implements RemoverEstudianteFichaPerfilUseCase {

    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemoverEstudianteFichaPerfilCommand entrada) {
        UUID fichaPerfil = entrada.fichaPerfil();
        UUID estudiante = entrada.estudiante();

        fichaPerfilValidator.validarFichaExiste(fichaPerfil);
        validarEstudianteExiste(estudiante);
        validarVinculoExiste(fichaPerfil, estudiante);

        estudianteFichaPerfilOutputPort.eliminar(fichaPerfil, estudiante);

        logger.info(FichasMessages.EstudianteFichaPerfil.LOG_REMOVIDO, fichaPerfil, estudiante);
    }

    private void validarEstudianteExiste(UUID estudiante) {
        if (!estudianteQueryOutputPort.existePorId(estudiante)) {
            throw new EstudianteNoEncontradoException(estudiante);
        }
    }

    private void validarVinculoExiste(UUID fichaPerfil, UUID estudiante) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfil, estudiante)) {
            throw new EstudianteFichaPerfilNoEncontradoException(estudiante, fichaPerfil);
        }
    }
}
