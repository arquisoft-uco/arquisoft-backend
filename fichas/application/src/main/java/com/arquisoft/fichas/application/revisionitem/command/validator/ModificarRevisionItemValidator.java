package com.arquisoft.fichas.application.revisionitem.command.validator;

import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;

import java.util.UUID;

public interface ModificarRevisionItemValidator {

    void validar(ModificacionRevisionItemDomain entrada, long cantidadRevisiones, UUID fichaPerfil,
                 boolean esPropietario);
}
