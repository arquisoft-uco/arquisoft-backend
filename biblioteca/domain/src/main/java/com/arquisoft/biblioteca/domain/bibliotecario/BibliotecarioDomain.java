package com.arquisoft.biblioteca.domain.bibliotecario;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class BibliotecarioDomain {

    public static final BibliotecarioDomain VACIO = BibliotecarioDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private BibliotecarioDomain() {}

    public static BibliotecarioDomain crear(UUID id, String identificador, String nombre, String email,
                                            Instant ocurridoEn) {
        var bibliotecario = new BibliotecarioDomain();
        var result = new ValidationResult();

        bibliotecario.setId(id, result);
        bibliotecario.setIdentificador(identificador, result);
        bibliotecario.setNombre(nombre, result);
        bibliotecario.setEmail(email, result);
        bibliotecario.setOcurridoEn(ocurridoEn, result);
        bibliotecario.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return bibliotecario;
    }

    public static BibliotecarioDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                  Instant ocurridoEn, Instant eliminadoEn) {
        var bibliotecario = new BibliotecarioDomain();
        bibliotecario.id = id;
        bibliotecario.identificador = identificador;
        bibliotecario.nombre = nombre;
        bibliotecario.email = email;
        bibliotecario.ocurridoEn = ocurridoEn;
        bibliotecario.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return bibliotecario;
    }

    public void reactivar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        this.eliminadoEn = UtilFecha.VACIO;
    }

    public void remover(Instant ocurridoEn) {
        this.eliminadoEn = ocurridoEn;
        this.ocurridoEn = ocurridoEn;
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                BibliotecaFields.Bibliotecario.ID,
                BibliotecaCodes.Bibliotecario.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                BibliotecaFields.Bibliotecario.IDENTIFICADOR,
                BibliotecaCodes.Bibliotecario.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                BibliotecaFields.Bibliotecario.NOMBRE,
                BibliotecaCodes.Bibliotecario.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                BibliotecaFields.Bibliotecario.EMAIL,
                BibliotecaCodes.Bibliotecario.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                BibliotecaFields.Bibliotecario.OCURRIDO_EN,
                BibliotecaCodes.Bibliotecario.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
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

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
