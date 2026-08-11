package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.secondaryport.EstudianteFichaPerfilOutputPort;
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
    public void vincularEstudiante(EstudianteFichaPerfilDomain relacion) {
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
    public void desvincularEstudiante(UUID fichaPerfilId, UUID estudianteId) {
        repository.deleteByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }
}
