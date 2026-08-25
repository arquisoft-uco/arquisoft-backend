package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.RepresentanteComiteCurriculumOutputPort;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.entity.RepresentanteComiteCurriculumEntity;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.mapper.RepresentanteComiteCurriculumJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteCurriculumCommandOutputAdapter implements RepresentanteComiteCurriculumOutputPort {

    private final RepresentanteComiteCurriculumCommandRepository repository;

    @Override
    public void agregarRepresentante(RepresentanteComiteCurriculumEntity entity) {
        var jpaEntity = RepresentanteComiteCurriculumJpaMapper.toJpaEntity(entity);
        repository.save(jpaEntity);
    }

    @Override
    public boolean existePorUsuario(UUID usuario) {
        return repository.existsByUsuario(usuario);
    }
}
