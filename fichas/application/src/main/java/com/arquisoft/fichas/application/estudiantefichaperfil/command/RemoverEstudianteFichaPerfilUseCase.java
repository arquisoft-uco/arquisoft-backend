package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiante.query.port.out.EstudianteQueryOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilInputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoverEstudianteFichaPerfilUseCase implements RemoverEstudianteFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteQueryOutputPort estudianteQueryOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverEstudianteFichaPerfilCommand command) {
        var fichaPerfilId = command.fichaPerfilId();
        var estudianteId = command.estudianteId();

        if (!fichaPerfilOutputPort.existsById(fichaPerfilId)) {
            throw new FichaPerfilNoEncontradaException(fichaPerfilId);
        }

        if (!estudianteQueryOutputPort.existsById(estudianteId)) {
            throw new EstudianteNoEncontradoException(estudianteId);
        }

        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaPerfilId, estudianteId)) {
            throw new EstudianteFichaPerfilNoEncontradoException(estudianteId, fichaPerfilId);
        }

        estudianteFichaPerfilOutputPort.eliminar(fichaPerfilId, estudianteId);

        log.info(FichasMessages.EstudianteFichaPerfil.LOG_REMOVIDO, fichaPerfilId, estudianteId);
    }
}
