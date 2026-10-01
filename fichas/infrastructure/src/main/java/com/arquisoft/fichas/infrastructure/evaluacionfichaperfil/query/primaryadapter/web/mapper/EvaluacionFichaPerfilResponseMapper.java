package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilResponseDTO;

public final class EvaluacionFichaPerfilResponseMapper {

    private EvaluacionFichaPerfilResponseMapper() {}

    public static EvaluacionFichaPerfilResponseDTO toResponse(EvaluacionFichaPerfilReadModel readModel) {
        return new EvaluacionFichaPerfilResponseDTO(
                readModel.id(),
                readModel.fichaPerfilId(),
                readModel.fechaCreacion(),
                readModel.estadoEvaluacion(),
                readModel.estadoEvaluacionNombre());
    }
}
