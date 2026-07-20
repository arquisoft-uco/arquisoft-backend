package com.arquisoft.fichas.infrastructure.estadoficha.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_ficha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;
}
