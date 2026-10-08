package com.arquisoft.fichas.application.observacionitem.command.validator;

import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;

public interface ModificarObservacionItemValidator {

    void validar(ModificacionObservacionItemDomain entrada, ContextoObservacionItem contexto,
                 long observacionesIguales);
}
