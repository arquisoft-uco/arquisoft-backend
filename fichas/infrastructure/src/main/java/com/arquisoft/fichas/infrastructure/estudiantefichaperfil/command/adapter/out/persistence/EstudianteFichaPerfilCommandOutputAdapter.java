package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilCommandOutputAdapter implements EstudianteFichaPerfilOutputPort {

    private final EstudianteFichaPerfilRepository repository;
    private final EstudianteFichaPerfilMapper mapper;

    @Override
    public void guardar(EstudianteFichaPerfilDomain relacion) {
        var entity = mapper.toEntity(relacion);
        repository.save(entity);
    }

    @Override
    public boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId) {
        return repository.existsByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }

    @Override
    public long contarPorFichaPerfilId(UUID fichaPerfilId) {
        return repository.countByFichaPerfilId(fichaPerfilId);
    }

    @Override
    public void eliminar(UUID fichaPerfilId, UUID estudianteId) {
        repository.deleteByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }
}
