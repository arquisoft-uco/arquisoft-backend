package com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.List;
import java.util.UUID;

public interface EstadoFichaPerfilQueryOutputPort {

    List<EstadoFichaPerfilReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante);

    PaginatedResult<EstadoFichaPerfilAsesorReadModel> consultarPorAsesor(EstadoFichaPerfilAsesorCriteria criteria);

    List<EstadoFichaPerfilReadModel> consultarPorFichaYRepresentante(UUID fichaPerfil, UUID representanteComite);

    List<EstadoFichaPerfilReadModel> consultarPorFicha(UUID fichaPerfil);
}
