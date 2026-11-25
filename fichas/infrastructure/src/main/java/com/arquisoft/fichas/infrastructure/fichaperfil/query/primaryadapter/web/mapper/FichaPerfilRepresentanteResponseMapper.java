package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionParticipanteResponseDTO;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionParticipanteResponseDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.FichaPerfilRepresentanteResponseDTO;

public final class FichaPerfilRepresentanteResponseMapper {

    private FichaPerfilRepresentanteResponseMapper() {}

    public static FichaPerfilRepresentanteResponseDTO toResponse(FichaPerfilRepresentanteReadModel readModel) {
        var ficha = FichaPerfilResponseMapper.toResponse(readModel.ficha());
        var evaluacion = readModel.evaluacion();

        return new FichaPerfilRepresentanteResponseDTO(
                ficha.id(),
                ficha.tituloProyecto(),
                ficha.asesorFicha(),
                ficha.estado(),
                new EvaluacionParticipanteResponseDTO(
                        evaluacion.id(),
                        new EstadoEvaluacionParticipanteResponseDTO(
                                evaluacion.estadoId(),
                                evaluacion.estadoNombre())));
    }
}
