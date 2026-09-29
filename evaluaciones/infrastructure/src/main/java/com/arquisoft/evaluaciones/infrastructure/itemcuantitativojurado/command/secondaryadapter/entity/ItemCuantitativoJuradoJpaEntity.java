package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity;

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
@Table(name = "item_cuantitativo_jurado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCuantitativoJuradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;

    @Column(name = "categoria_id", nullable = false, columnDefinition = "uuid")
    private UUID categoriaId;

    @Column(name = "valor", nullable = false)
    private Integer valor;
}
