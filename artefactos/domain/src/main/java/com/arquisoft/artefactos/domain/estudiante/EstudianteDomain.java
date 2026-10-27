package com.arquisoft.artefactos.domain.estudiante;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteDomain {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant eliminadoEn;

    private EstudianteDomain() {}

    public static EstudianteDomain reconstruir(
            UUID id, String identificador, String nombre, String email, Instant eliminadoEn) {
        var estudiante = new EstudianteDomain();
        estudiante.id = id;
        estudiante.identificador = identificador;
        estudiante.nombre = nombre;
        estudiante.email = email;
        estudiante.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return estudiante;
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }
}
