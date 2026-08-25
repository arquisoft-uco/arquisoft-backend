package com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.entity.RepresentanteComiteCurriculumEntity;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.RepresentanteComiteCurriculumDomain;

public final class RepresentanteComiteCurriculumMapper {

    private RepresentanteComiteCurriculumMapper() {
    }

    public static RepresentanteComiteCurriculumDomain toDomain(RepresentanteComiteCurriculumEntity entity) {
        return RepresentanteComiteCurriculumDomain.reconstruir(entity.usuario());
    }

    public static RepresentanteComiteCurriculumEntity toEntity(RepresentanteComiteCurriculumDomain domain) {
        return new RepresentanteComiteCurriculumEntity(domain.getUsuario());
    }
}
