package com.arquisoft.fichas.application.estudiantefichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

public final class LimiteEstudiantesExcedidoException extends ApplicationException {

    public LimiteEstudiantesExcedidoException() {
        super(
            FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO.formatted(
                FichasMessages.FichaPerfil.ESTUDIANTES_MAX
            ),
            FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO
        );
    }
}
