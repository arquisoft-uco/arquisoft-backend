package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.port.out.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilQueryOutputAdapter implements EstudianteFichaPerfilQueryOutputPort {

    private final EstudianteFichaPerfilJpaRepository jpaRepository;

    @Override
    public boolean existePorEstudianteYFicha(UUID estudianteId, UUID fichaPerfilId) {
        return jpaRepository.existsByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }
}
