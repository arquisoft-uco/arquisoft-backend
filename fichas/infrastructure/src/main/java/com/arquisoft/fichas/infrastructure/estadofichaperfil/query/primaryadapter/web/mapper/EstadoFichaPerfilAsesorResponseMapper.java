package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.dto.EstadoFichaPerfilAsesorResponseDTO;

public final class EstadoFichaPerfilAsesorResponseMapper {

    private EstadoFichaPerfilAsesorResponseMapper() {}

    public static EstadoFichaPerfilAsesorResponseDTO toResponse(EstadoFichaPerfilAsesorReadModel readModel) {
        return new EstadoFichaPerfilAsesorResponseDTO(
                readModel.fichaPerfil(),
                readModel.tituloProyecto(),
                readModel.estadoId(),
                readModel.estadoNombre(),
                readModel.fechaActualizacion());
    }
}
