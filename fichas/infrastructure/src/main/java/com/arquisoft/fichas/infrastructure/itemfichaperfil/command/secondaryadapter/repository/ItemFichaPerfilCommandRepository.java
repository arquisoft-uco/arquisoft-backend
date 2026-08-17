package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilCommandRepository extends JpaRepository<ItemFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId);

    @Query("SELECT i.fichaPerfilId FROM ItemFichaPerfilJpaEntity i WHERE i.id = :id")
    Optional<UUID> obtenerFichaPerfilId(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ItemFichaPerfilJpaEntity i SET i.contenido = :contenido WHERE i.id = :id")
    int actualizarContenido(@Param("id") UUID id, @Param("contenido") String contenido);
}
