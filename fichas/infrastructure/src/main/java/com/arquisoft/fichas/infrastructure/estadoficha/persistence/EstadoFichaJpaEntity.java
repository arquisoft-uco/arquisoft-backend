package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "estado_ficha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaJpaEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;
}
