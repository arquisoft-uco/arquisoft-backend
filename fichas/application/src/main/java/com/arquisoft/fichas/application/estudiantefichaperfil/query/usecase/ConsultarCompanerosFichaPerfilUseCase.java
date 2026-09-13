package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarCompanerosFichaPerfilUseCase
        extends UseCase<EstudianteFichaPerfilCompaneroCriteria, List<EstudianteFichaPerfilReadModel>> {}
