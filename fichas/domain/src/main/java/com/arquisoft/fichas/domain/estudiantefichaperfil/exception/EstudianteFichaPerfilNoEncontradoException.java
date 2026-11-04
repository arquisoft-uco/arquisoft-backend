package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public final class EstudianteFichaPerfilNoEncontradoException extends DomainException {

    public EstudianteFichaPerfilNoEncontradoException(UUID estudianteId, UUID fichaPerfilId) {
        super(BaseError.conDetalle(
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO,
                Mensajes.formatear(EstudianteFichaPerfilKey.ERROR_RELACION_NO_ENCONTRADA),
                Mensajes.formatear(EstudianteFichaPerfilKey.ERROR_RELACION_NO_ENCONTRADA_DETALLE, estudianteId, fichaPerfilId)
        ));
    }
}
