package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarParticipacionesArtefactoInteractor
        extends Interactor<ConsultarParticipacionesArtefactoQuery, List<ParticipacionArtefactoReadModel>> {}
