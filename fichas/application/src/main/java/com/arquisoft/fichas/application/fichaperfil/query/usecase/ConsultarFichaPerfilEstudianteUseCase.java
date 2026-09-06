package com.arquisoft.fichas.application.fichaperfil.query.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.Optional;

public interface ConsultarFichaPerfilEstudianteUseCase
        extends UseCase<FichaPerfilEstudianteCriteria, Optional<FichaPerfilEstudianteReadModel>> {
}
