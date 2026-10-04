package com.arquisoft.fichas.application.revisionitem.command.validator;

import java.util.UUID;

public interface RemoverRevisionItemValidator {

    void validar(UUID revisionItem, UUID asesorSolicitante, UUID fichaPerfil, UUID asesorDeLaFicha,
                 boolean revisionExiste, String estadoRevisionId);
}
