package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarFichasPerfilEstudianteInteractor
        extends Interactor<ConsultarFichasPerfilEstudianteQuery, List<FichaPerfilEstudianteReadModel>> {
}
