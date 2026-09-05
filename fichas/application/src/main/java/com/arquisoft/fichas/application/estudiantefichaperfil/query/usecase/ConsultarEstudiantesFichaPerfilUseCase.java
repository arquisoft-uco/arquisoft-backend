package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarEstudiantesFichaPerfilUseCase
        extends UseCase<EstudianteFichaPerfilCriteria, List<EstudianteFichaPerfilReadModel>> {}
