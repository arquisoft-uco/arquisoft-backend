package com.arquisoft.fichas.application.tipoitem.command.secondaryport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoItemEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "nombre", nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;
}
