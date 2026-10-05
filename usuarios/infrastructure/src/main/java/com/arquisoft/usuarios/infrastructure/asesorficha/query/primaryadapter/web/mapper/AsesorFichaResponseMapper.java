package com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;

public final class AsesorFichaResponseMapper {

    private AsesorFichaResponseMapper() {}

    public static AsesorFichaResponseDTO toResponse(AsesorFichaReadModel readModel) {
        return new AsesorFichaResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}
