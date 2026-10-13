package com.arquisoft.fichas.application.fichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;

import java.util.List;

public interface FichaPerfilEstudianteQueryOutputPort {

    List<FichaPerfilEstudianteReadModel> consultarPorEstudiante(FichaPerfilEstudianteCriteria criteria);
}
