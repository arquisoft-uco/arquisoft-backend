package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.FichaPerfilResponseDTO;

public final class FichaPerfilResponseMapper {

    private FichaPerfilResponseMapper() {}

    public static FichaPerfilResponseDTO toResponse(FichaPerfilReadModel readModel) {
        return new FichaPerfilResponseDTO(
                readModel.id(),
                readModel.tituloProyecto(),
                new AsesorFichaResponseDTO(
                        readModel.asesorFicha().id(),
                        readModel.asesorFicha().identificador(),
                        readModel.asesorFicha().nombre(),
                        readModel.asesorFicha().email()));
    }
}
