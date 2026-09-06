package com.arquisoft.fichas.application.fichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;

import java.util.Optional;

public interface FichaPerfilEstudianteQueryOutputPort {

    Optional<FichaPerfilEstudianteReadModel> consultar(FichaPerfilEstudianteCriteria criteria);
}
