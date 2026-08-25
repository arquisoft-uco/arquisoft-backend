package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.entity.RepresentanteComiteCurriculumEntity;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.entity.RepresentanteComiteCurriculumJpaEntity;

public final class RepresentanteComiteCurriculumJpaMapper {

    private RepresentanteComiteCurriculumJpaMapper() {
    }

    public static RepresentanteComiteCurriculumEntity toEntity(RepresentanteComiteCurriculumJpaEntity jpa) {
        return new RepresentanteComiteCurriculumEntity(jpa.getUsuario());
    }

    public static RepresentanteComiteCurriculumJpaEntity toJpaEntity(RepresentanteComiteCurriculumEntity entity) {
        return RepresentanteComiteCurriculumJpaEntity.builder()
                .usuario(entity.usuario())
                .build();
    }
}
