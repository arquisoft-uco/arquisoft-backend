package com.arquisoft.entregables.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoAccesoQueryRepository
        extends QueryRepository<AsesorProyectoGradoAccesoJpaQueryEntity, UUID> {

    @Query("""
            SELECT CASE WHEN a.finalizadoEn IS NULL THEN a.asesorId END
            FROM AsesorProyectoGradoAccesoJpaQueryEntity a
            WHERE a.proyectoGradoId = :proyectoGradoId
            ORDER BY a.ocurridoEn DESC, a.asesorId
            LIMIT 1
            """)
    Optional<UUID> buscarAsesorVigentePorProyectoGrado(@Param("proyectoGradoId") UUID proyectoGradoId);
}
