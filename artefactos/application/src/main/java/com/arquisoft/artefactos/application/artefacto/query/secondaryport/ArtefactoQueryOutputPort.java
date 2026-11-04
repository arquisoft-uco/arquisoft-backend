package com.arquisoft.artefactos.application.artefacto.query.secondaryport;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ArtefactoQueryOutputPort {

    PaginatedResult<ArtefactoReadModel> consultarTodosAsesor(ArtefactoAsesorCriteria criteria);

    PaginatedResult<ArtefactoEstudianteReadModel> consultarTodosEstudiante(ArtefactoEstudianteCriteria criteria);

    PaginatedResult<ArtefactoSinAvanceReadModel> consultarSinAvance(ArtefactoSinAvanceCriteria criteria);
}
