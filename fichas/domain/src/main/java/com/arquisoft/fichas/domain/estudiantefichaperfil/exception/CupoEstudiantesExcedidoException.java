package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

public class CupoEstudiantesExcedidoException extends DomainException {

    public CupoEstudiantesExcedidoException(int maximo) {
        super(
                Mensajes.formatear(EstudianteFichaPerfilKey.ERROR_LIMITE_EXCEDIDO, maximo),
                FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO);
    }
}
