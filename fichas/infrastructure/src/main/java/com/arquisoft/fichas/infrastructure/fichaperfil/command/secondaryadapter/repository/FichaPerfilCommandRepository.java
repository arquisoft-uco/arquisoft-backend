package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FichaPerfilCommandRepository extends JpaRepository<FichaPerfilJpaEntity, UUID> {

    boolean existsByTituloProyecto(String tituloProyecto);

    boolean existsByTituloProyectoAndIdNot(String tituloProyecto, UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilJpaEntity f SET f.asesorFicha = :asesorFicha WHERE f.id = :id")
    int actualizarAsesorFicha(@Param("id") UUID id, @Param("asesorFicha") AsesorFichaJpaEntity asesorFicha);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilJpaEntity f SET f.tituloProyecto = :tituloProyecto WHERE f.id = :id")
    int actualizarTitulo(@Param("id") UUID id, @Param("tituloProyecto") String tituloProyecto);
}
