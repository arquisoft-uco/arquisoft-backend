package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.DomainException;

public class CupoEstudiantesExcedidoException extends DomainException {

    public CupoEstudiantesExcedidoException(int maximo) {
        super(
                Messages.formatear(FichasKeys.EstudianteFichaPerfil.ERROR_LIMITE_EXCEDIDO, maximo),
                FichasCodes.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO);
    }
}
