package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;

import java.util.UUID;

public interface AgregarRevisionItemValidator {

    void validar(AgregacionRevisionItemDomain entrada, boolean itemExiste, UUID fichaPerfil, UUID asesorFicha,
                 long cantidadRevisiones);
}
