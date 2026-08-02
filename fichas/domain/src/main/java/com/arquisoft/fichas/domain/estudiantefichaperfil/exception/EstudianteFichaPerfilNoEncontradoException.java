package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class EstudianteFichaPerfilNoEncontradoException extends ApplicationException {

    public EstudianteFichaPerfilNoEncontradoException(UUID estudianteId, UUID fichaPerfilId) {
        super(
                FichasMessages.EstudianteFichaPerfil.RELACION_NO_ENCONTRADA_MSG.formatted(
                        estudianteId,
                        fichaPerfilId
                ),
                FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO
        );
    }
}
