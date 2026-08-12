package com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "revision_item")
@Getter
@NoArgsConstructor
public class RevisionItemEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "estado_revision_id", nullable = false, length = 50)
    private String estadoRevisionId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
