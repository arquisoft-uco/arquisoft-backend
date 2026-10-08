package com.arquisoft.fichas.application.revisionitem.command.validator;

import java.util.UUID;

public interface MarcarRevisionItemComoVisualizadaValidator {

    void validar(UUID revisionItem, UUID estudiante, UUID fichaPerfil, boolean revisionExiste,
                 boolean esPropietario, String estadoRevisionId);
}
