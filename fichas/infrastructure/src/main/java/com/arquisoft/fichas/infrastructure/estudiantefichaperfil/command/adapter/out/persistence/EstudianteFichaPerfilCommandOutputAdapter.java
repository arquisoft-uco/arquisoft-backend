package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilCommandOutputAdapter implements EstudianteFichaPerfilOutputPort {

    private final EstudianteFichaPerfilJpaRepository jpaRepository;
    private final EstudianteFichaPerfilMapper mapper;

    @Override
    public void guardar(EstudianteFichaPerfilAggregate relacion) {
        var entity = mapper.toJpaEntity(relacion);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId) {
        return jpaRepository.existsByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }

    @Override
    public long contarPorFichaPerfilId(UUID fichaPerfilId) {
        return jpaRepository.countByFichaPerfilId(fichaPerfilId);
    }

    @Override
    public void eliminar(UUID fichaPerfilId, UUID estudianteId) {
        jpaRepository.deleteByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }
}
