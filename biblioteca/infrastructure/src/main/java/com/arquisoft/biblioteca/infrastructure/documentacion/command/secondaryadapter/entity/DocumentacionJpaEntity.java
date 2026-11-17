package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documentacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentacionJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @Column(name = "fecha_publicacion", nullable = false)
    private Instant fechaPublicacion;

    @Column(name = "entregable_proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID entregableProyectoGradoId;

    @Column(name = "bibliotecario_id", nullable = false, columnDefinition = "UUID")
    private UUID bibliotecarioId;

    @Column(name = "estado_documentacion_id", nullable = false, length = 60)
    private String estadoDocumentacion;
}
