package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity;

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
@Table(name = "observacion_item_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionItemJuradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_cuantitativa_jurado_id", nullable = false, columnDefinition = "uuid")
    private UUID evaluacionCuantitativaJuradoId;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;
}
