package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity;

import com.arquisoft.fichas.application.tipoitem.command.secondaryport.entity.TipoItemEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFichaPerfilEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "UUID")
    private UUID fichaPerfilId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_item_id", nullable = false)
    private TipoItemEntity tipoItem;

    @Column(name = "contenido", nullable = false, length = 7000)
    private String contenido;
}
