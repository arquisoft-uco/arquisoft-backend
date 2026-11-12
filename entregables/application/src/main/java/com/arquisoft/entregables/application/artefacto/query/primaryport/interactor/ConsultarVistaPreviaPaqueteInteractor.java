package com.arquisoft.entregables.application.artefacto.query.primaryport.interactor;

import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarVistaPreviaPaqueteInteractor
        extends Interactor<ConsultarVistaPreviaPaqueteQuery, List<ArtefactoVistaPreviaReadModel>> {
}
