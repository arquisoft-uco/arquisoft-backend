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
               efp.estudiante_id AS estudiante_id,
               t.id              AS tipo_item_id,
               t.nombre          AS tipo_item_nombre,
               i.contenido       AS contenido
        FROM item i
                 JOIN estudiante_ficha_perfil efp ON efp.ficha_perfil_id = i.ficha_perfil_id
                 JOIN tipo_item t ON t.id = i.tipo_item_id
        """)
@Synchronize({"item", "estudiante_ficha_perfil", "tipo_item"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFichaPerfilEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "tipo_item_id")
    private String tipoItemId;

    @Column(name = "tipo_item_nombre")
    private String tipoItemNombre;

    @Column(name = "contenido")
    private String contenido;
}
