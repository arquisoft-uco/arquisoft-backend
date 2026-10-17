package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "mapa_ruta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapaRutaJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
}
