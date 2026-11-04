package com.arquisoft.mapas_ruta.infrastructure.tarea.command.secondaryadapter.entity;

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
@Table(name = "tarea")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "mapa_ruta_id", nullable = false, columnDefinition = "uuid")
    private UUID mapaRutaId;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "estado_item_id", nullable = false, length = 60)
    private String estadoItemId;
}
