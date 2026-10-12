package com.arquisoft.fichas.application.estadofichaperfil.query.usecase;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstadosFichaPerfilRepresentanteUseCase
        extends UseCase<EstadoFichaPerfilRepresentanteCriteria, List<EstadoFichaPerfilReadModel>> {}
