package com.arquisoft.fichas.application.observacionitem.command.validator;

import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;

public interface RemoverObservacionItemValidator {

    void validar(RemocionObservacionItemDomain entrada, ContextoObservacionItem contexto);
}
