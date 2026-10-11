package com.arquisoft.fichas.application.observacionitem.command.validator;

import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;

import java.util.UUID;

public interface AgregarObservacionItemValidator {

    void validar(AgregacionObservacionItemDomain entrada, boolean revisionExiste,
                 String estadoRevisionId, UUID fichaPerfil, UUID asesorDeLaFicha,
                 long observacionesIguales);
}
