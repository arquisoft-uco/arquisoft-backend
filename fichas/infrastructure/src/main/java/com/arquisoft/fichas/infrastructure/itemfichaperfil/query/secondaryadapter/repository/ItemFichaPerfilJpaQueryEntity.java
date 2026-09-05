package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT i.id              AS id,
               i.ficha_perfil_id AS ficha_perfil_id,
               f.asesor_ficha_id AS asesor_id,
               t.id              AS tipo_item_id,
               t.nombre          AS tipo_item_nombre,
               i.contenido       AS contenido
        FROM item i
                 JOIN ficha_perfil f ON f.id = i.ficha_perfil_id
                 JOIN tipo_item t ON t.id = i.tipo_item_id
        """)
@Synchronize({"item", "ficha_perfil", "tipo_item"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFichaPerfilJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "tipo_item_id")
    private String tipoItemId;

    @Column(name = "tipo_item_nombre")
    private String tipoItemNombre;

    @Column(name = "contenido")
    private String contenido;
}
