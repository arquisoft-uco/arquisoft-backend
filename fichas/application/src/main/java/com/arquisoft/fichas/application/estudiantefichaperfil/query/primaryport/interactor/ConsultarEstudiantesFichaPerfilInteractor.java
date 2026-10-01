package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstudiantesFichaPerfilInteractor
        extends Interactor<ConsultarEstudiantesFichaPerfilQuery, List<EstudianteFichaPerfilReadModel>> {}
