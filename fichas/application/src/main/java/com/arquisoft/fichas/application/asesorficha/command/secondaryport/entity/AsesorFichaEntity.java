package com.arquisoft.fichas.infrastructure.asesorficha.persistence;

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
@Table(name = "asesor_ficha")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorFichaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "identificador", nullable = false, length = 30)
    private String identificador;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 50)
    private String email;
}
