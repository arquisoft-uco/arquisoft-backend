package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilEstudianteResponseDTO;
import com.arquisoft.fichas.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteResponseDTO;

public final class EvaluacionFichaPerfilEstudianteResponseMapper {

    private EvaluacionFichaPerfilEstudianteResponseMapper() {}

    public static EvaluacionFichaPerfilEstudianteResponseDTO toResponse(EvaluacionFichaPerfilEstudianteReadModel readModel) {
        var representante = readModel.representanteComite();
        return new EvaluacionFichaPerfilEstudianteResponseDTO(
                readModel.id(),
                readModel.fichaPerfil(),
                readModel.fechaCreacion(),
                readModel.estadoEvaluacion(),
                readModel.estadoEvaluacionNombre(),
                new RepresentanteComiteResponseDTO(representante.id(), representante.nombre()));
    }
}
