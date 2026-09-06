package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.ContactoEstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilCommandRepository
        extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    long countByFichaPerfilId(UUID fichaPerfilId);

    void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);

    // Un solo JOIN contra la réplica local de estudiante en vez de resolver ids y luego,
    // aparte, sus datos: evita dos viajes a la misma base para armar el contacto.
    @Query("""
            SELECT new com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.ContactoEstudianteEntity(e.nombre, e.email)
            FROM EstudianteFichaPerfilJpaEntity ef
            JOIN EstudianteJpaEntity e ON e.id = ef.estudianteId
            WHERE ef.fichaPerfilId = :fichaPerfilId
            """)
    List<ContactoEstudianteEntity> findContactosByFichaPerfilId(@Param("fichaPerfilId") UUID fichaPerfilId);
}
