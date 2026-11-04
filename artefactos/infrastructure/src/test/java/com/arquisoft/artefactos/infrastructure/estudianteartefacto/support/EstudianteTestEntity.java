package com.arquisoft.artefactos.infrastructure.estudianteartefacto.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estudiante")
public class EstudianteTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn = Instant.EPOCH;

    @Column(name = "eliminado_en")
    private Instant eliminadoEn;

    protected EstudianteTestEntity() {}

    public EstudianteTestEntity(UUID id, String identificador, String nombre, String email, Instant eliminadoEn) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
        this.eliminadoEn = eliminadoEn;
    }
}
