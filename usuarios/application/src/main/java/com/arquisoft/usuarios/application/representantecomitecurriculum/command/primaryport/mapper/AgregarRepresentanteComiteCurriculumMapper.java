package com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.mapper;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model.AgregarRepresentanteComiteCurriculumCommand;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.RepresentanteComiteCurriculumDomain;

public final class AgregarRepresentanteComiteCurriculumMapper {

    private AgregarRepresentanteComiteCurriculumMapper() {
    }

    public static RepresentanteComiteCurriculumDomain toDomain(AgregarRepresentanteComiteCurriculumCommand command) {
        var representante = RepresentanteComiteCurriculumDomain.crear(command.usuario());
        return RepresentanteComiteCurriculumDomain.crear(command.usuario());
    }
}
