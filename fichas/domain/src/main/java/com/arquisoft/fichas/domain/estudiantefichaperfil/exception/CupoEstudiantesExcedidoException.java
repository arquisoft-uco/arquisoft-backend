package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.FichasMessages;

public class CupoEstudiantesExcedidoException extends DomainException {

    public CupoEstudiantesExcedidoException(int maximo) {
        super(
                FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO_MSG.formatted(maximo),
                FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO);
    }
}
