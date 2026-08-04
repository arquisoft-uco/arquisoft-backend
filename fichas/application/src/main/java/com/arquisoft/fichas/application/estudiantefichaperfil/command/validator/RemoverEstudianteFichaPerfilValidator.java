package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import java.util.UUID;

public interface RemoverEstudianteFichaPerfilValidator {

    void validar(UUID fichaPerfil, UUID estudiante);
}
