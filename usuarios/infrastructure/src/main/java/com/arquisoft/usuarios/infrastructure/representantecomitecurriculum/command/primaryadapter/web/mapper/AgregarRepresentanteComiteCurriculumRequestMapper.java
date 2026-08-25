package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model.AgregarRepresentanteComiteCurriculumCommand;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web.dto.AgregarRepresentanteComiteCurriculumRequestDTO;

public final class AgregarRepresentanteComiteCurriculumRequestMapper {

    private AgregarRepresentanteComiteCurriculumRequestMapper() {
    }

    public static AgregarRepresentanteComiteCurriculumCommand toCommand(AgregarRepresentanteComiteCurriculumRequestDTO dto) {
        return AgregarRepresentanteComiteCurriculumCommand.crear(dto.usuario());
    }
}
