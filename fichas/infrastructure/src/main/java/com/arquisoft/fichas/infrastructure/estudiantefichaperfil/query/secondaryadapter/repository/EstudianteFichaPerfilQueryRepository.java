package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilQueryRepository
        extends QueryRepository<EstudianteFichaPerfilJpaQueryEntity, UUID> {

    List<EstudianteFichaPerfilJpaQueryEntity> findByFichaPerfilIdOrderByNombreAsc(UUID fichaPerfilId);
}
