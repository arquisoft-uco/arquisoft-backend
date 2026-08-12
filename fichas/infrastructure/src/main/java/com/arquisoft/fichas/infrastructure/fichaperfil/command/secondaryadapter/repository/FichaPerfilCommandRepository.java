package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FichaPerfilCommandRepository extends JpaRepository<FichaPerfilEntity, UUID> {

    boolean existsByTituloProyecto(String tituloProyecto);

    boolean existsByTituloProyectoAndIdNot(String tituloProyecto, UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilEntity f SET f.asesorFicha = :asesorFicha WHERE f.id = :id")
    int actualizarAsesorFicha(@Param("id") UUID id, @Param("asesorFicha") AsesorFichaEntity asesorFicha);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FichaPerfilEntity f SET f.tituloProyecto = :tituloProyecto WHERE f.id = :id")
    int actualizarTitulo(@Param("id") UUID id, @Param("tituloProyecto") String tituloProyecto);
}
