package com.arquisoft.fichas.infrastructure.representantecomite.persistence;

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
@Table(name = "representante_comite_curriculum")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepresentanteComiteEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "identificador", nullable = false, length = 30)
    private String identificador;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 50)
    private String email;
}
