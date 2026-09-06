package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.Optional;

public interface ConsultarFichaPerfilEstudianteInteractor
        extends Interactor<ConsultarFichaPerfilEstudianteQuery, Optional<FichaPerfilEstudianteReadModel>> {
}
