package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilAsesorResponseDTO;
import com.arquisoft.fichas.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteResponseDTO;

public final class EvaluacionFichaPerfilAsesorResponseMapper {

    private EvaluacionFichaPerfilAsesorResponseMapper() {}

    public static EvaluacionFichaPerfilAsesorResponseDTO toResponse(EvaluacionFichaPerfilAsesorReadModel readModel) {
        var representante = readModel.representanteComite();
        return new EvaluacionFichaPerfilAsesorResponseDTO(
                readModel.id(),
                readModel.fichaPerfil(),
                readModel.tituloProyecto(),
                readModel.fechaCreacion(),
                readModel.estadoEvaluacion(),
                readModel.estadoEvaluacionNombre(),
                new RepresentanteComiteResponseDTO(representante.id(), representante.nombre()));
    }
}
