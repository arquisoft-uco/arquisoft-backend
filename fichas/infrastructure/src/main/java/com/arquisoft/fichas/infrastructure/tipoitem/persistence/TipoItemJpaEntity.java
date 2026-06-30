package com.arquisoft.fichas.infrastructure.tipoitem.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoItemJpaEntity {

    @Id
    @Column(nullable = false, length = 50)
    private String id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;
}
