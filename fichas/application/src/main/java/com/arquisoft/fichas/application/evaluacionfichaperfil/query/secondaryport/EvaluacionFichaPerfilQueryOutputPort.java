package com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.List;
import java.util.UUID;

public interface EvaluacionFichaPerfilQueryOutputPort {

    List<EvaluacionFichaPerfilReadModel> consultarPorFichaYRepresentante(UUID fichaPerfil, UUID representanteComite);

    List<EvaluacionFichaPerfilEstudianteReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante);

    List<EvaluacionFichaPerfilCoordinadorReadModel> consultarPorFicha(UUID fichaPerfil);

    PaginatedResult<EvaluacionFichaPerfilAsesorReadModel> consultarPorAsesor(EvaluacionFichaPerfilAsesorCriteria criteria);
}
