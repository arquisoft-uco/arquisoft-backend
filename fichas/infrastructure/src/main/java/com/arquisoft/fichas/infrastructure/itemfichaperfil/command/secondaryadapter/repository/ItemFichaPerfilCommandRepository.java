package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilRepository extends JpaRepository<ItemFichaPerfilEntity, UUID> {

    boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId);

    @Query("SELECT i.fichaPerfilId FROM ItemFichaPerfilEntity i WHERE i.id = :id")
    Optional<UUID> obtenerFichaPerfilId(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ItemFichaPerfilEntity i SET i.contenido = :contenido WHERE i.id = :id")
    int actualizarContenido(@Param("id") UUID id, @Param("contenido") String contenido);
}
