package com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "autor_documentacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorDocumentacionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "autor_id", nullable = false, columnDefinition = "UUID")
    private UUID autorId;

    @Column(name = "documentacion_id", nullable = false, columnDefinition = "UUID")
    private UUID documentacionId;
}
