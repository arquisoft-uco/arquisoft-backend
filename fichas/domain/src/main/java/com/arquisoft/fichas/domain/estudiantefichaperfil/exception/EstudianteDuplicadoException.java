package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;
import java.util.UUID;

public final class EstudianteDuplicadoException extends ApplicationException {

    public EstudianteDuplicadoException(UUID estudianteId) {
        super(
            Mensajes.formatear(EstudianteFichaPerfilKey.ERROR_DUPLICADO, estudianteId),
            FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO
        );
    }
}
