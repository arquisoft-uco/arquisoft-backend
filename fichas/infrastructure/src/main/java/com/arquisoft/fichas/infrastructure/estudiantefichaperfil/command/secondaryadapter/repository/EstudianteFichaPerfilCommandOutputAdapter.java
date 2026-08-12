package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilCommandOutputAdapter implements EstudianteFichaPerfilOutputPort {

    private final EstudianteFichaPerfilCommandRepository repository;

    @Override
    public void vincularEstudiante(EstudianteFichaPerfilEntity relacion) {
        repository.save(relacion);
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
