package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper.EstudianteFichaPerfilResponseMapper;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.EstadoFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.FichaPerfilEstudianteResponseDTO;

public final class FichaPerfilEstudianteResponseMapper {

    private FichaPerfilEstudianteResponseMapper() {}

    public static FichaPerfilEstudianteResponseDTO toResponse(FichaPerfilEstudianteReadModel r) {
        return new FichaPerfilEstudianteResponseDTO(
                r.id(),
                r.tituloProyecto(),
                new AsesorFichaResponseDTO(r.asesorFicha().id(), r.asesorFicha().identificador(),
                        r.asesorFicha().nombre(), r.asesorFicha().email()),
                new EstadoFichaPerfilResponseDTO(r.estado().id(), r.estado().nombre(), r.estado().fechaActualizacion()),
                r.estudiantes().stream().map(EstudianteFichaPerfilResponseMapper::toResponse).toList());
    }
}
