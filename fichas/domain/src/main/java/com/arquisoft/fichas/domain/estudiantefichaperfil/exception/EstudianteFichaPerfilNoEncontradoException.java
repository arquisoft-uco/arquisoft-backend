package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class EstudianteFichaPerfilNoEncontradoException extends ApplicationException {

    public EstudianteFichaPerfilNoEncontradoException(UUID estudianteId, UUID fichaPerfilId) {
        super(
                Mensajes.formatear(EstudianteFichaPerfilKey.ERROR_RELACION_NO_ENCONTRADA,
                        estudianteId,
                        fichaPerfilId
                ),
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO
        );
    }
}
