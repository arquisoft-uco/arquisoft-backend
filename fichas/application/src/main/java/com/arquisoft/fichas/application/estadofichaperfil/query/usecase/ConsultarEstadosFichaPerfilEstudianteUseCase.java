package com.arquisoft.fichas.application.estadofichaperfil.query.usecase;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstadosFichaPerfilEstudianteUseCase
        extends UseCase<EstadoFichaPerfilEstudianteCriteria, List<EstadoFichaPerfilReadModel>> {}
