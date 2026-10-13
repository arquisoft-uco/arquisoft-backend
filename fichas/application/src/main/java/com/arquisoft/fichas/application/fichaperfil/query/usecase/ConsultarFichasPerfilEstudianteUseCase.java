package com.arquisoft.fichas.application.fichaperfil.query.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarFichasPerfilEstudianteUseCase
        extends UseCase<FichaPerfilEstudianteCriteria, List<FichaPerfilEstudianteReadModel>> {
}
