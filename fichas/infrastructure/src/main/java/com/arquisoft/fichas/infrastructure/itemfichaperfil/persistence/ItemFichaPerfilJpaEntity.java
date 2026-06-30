package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFichaPerfilJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "UUID")
    private UUID fichaPerfilId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_item_id", nullable = false)
    private TipoItemJpaEntity tipoItem;

    @Column(name = "contenido", nullable = false, length = 7000)
    private String contenido;
}
