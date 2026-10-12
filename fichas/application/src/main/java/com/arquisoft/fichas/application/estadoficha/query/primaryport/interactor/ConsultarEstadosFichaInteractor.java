package com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor;

import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstadosFichaInteractor extends Interactor<ConsultarEstadosFichaQuery, List<EstadoFichaReadModel>> {
}
