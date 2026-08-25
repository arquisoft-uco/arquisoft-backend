package com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.entity.RepresentanteComiteCurriculumEntity;

import java.util.UUID;

public interface RepresentanteComiteCurriculumOutputPort {

    void agregarRepresentante (RepresentanteComiteCurriculumEntity entity);

    boolean existePorUsuario(UUID usuario);
}
