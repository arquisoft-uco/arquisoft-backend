package com.arquisoft.fichas.application.observacionitem.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;

public interface ReabrirObservacionItemValidator {

    void validar(ReaperturaObservacionItemDomain entrada, ContextoObservacionItem contexto, FichaPerfilDomain ficha);
}
