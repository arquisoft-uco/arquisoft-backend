package com.arquisoft.fichas.application.observacionitem.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;

public interface CerrarObservacionItemValidator {

    void validar(CierreObservacionItemDomain entrada, ContextoObservacionItem contexto, FichaPerfilDomain ficha);
}
