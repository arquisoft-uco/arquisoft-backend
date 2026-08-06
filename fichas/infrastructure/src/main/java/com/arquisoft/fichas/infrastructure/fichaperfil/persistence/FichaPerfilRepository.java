package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FichaPerfilRepository extends JpaRepository<FichaPerfilEntity, UUID>,
        JpaSpecificationExecutor<FichaPerfilEntity> {

    @EntityGraph(attributePaths = "asesorFicha")
    Page<FichaPerfilEntity> findAll(Specification<FichaPerfilEntity> spec, Pageable pageable);

    boolean existsByTituloProyecto(String tituloProyecto);

    boolean existsByTituloProyectoAndIdNot(String tituloProyecto, UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilEntity f SET f.asesorFicha = :asesorFicha WHERE f.id = :id")
    int actualizarAsesorFicha(@Param("id") UUID id, @Param("asesorFicha") AsesorFichaEntity asesorFicha);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilEntity f SET f.tituloProyecto = :tituloProyecto WHERE f.id = :id")
    int actualizarTitulo(@Param("id") UUID id, @Param("tituloProyecto") String tituloProyecto);
}
