package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilCoordinadorResponseDTO;
import com.arquisoft.fichas.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteResponseDTO;

public final class EvaluacionFichaPerfilCoordinadorResponseMapper {

    private EvaluacionFichaPerfilCoordinadorResponseMapper() {}

    public static EvaluacionFichaPerfilCoordinadorResponseDTO toResponse(EvaluacionFichaPerfilCoordinadorReadModel readModel) {
        var representante = readModel.representanteComite();
        return new EvaluacionFichaPerfilCoordinadorResponseDTO(
                readModel.id(),
                readModel.fichaPerfil(),
                readModel.fechaCreacion(),
                readModel.estadoEvaluacion(),
                readModel.estadoEvaluacionNombre(),
                new RepresentanteComiteResponseDTO(representante.id(), representante.nombre()));
    }
}
