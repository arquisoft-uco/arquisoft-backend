package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilQueryRepository
        extends QueryRepository<EstudianteFichaPerfilJpaQueryEntity, UUID> {

    List<EstudianteFichaPerfilJpaQueryEntity> findByFichaPerfilIdOrderByNombreAsc(UUID fichaPerfilId);

    @Query("""
            SELECT e FROM EstudianteFichaPerfilJpaQueryEntity e
            WHERE e.fichaPerfilId = :fichaPerfil
              AND e.estudianteId <> :estudiante
              AND EXISTS (
                  SELECT 1 FROM EstudianteFichaPerfilJpaQueryEntity propio
                  WHERE propio.fichaPerfilId = e.fichaPerfilId
                    AND propio.estudianteId = :estudiante
              )
            ORDER BY e.nombre ASC
            """)
    List<EstudianteFichaPerfilJpaQueryEntity> findCompanerosByFichaPerfilIdAndEstudianteId(
            @Param("fichaPerfil") UUID fichaPerfil,
            @Param("estudiante") UUID estudiante);
}
