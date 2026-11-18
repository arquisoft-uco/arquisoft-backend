package com.arquisoft.biblioteca.domain.autor;

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

public final class AutorDomain {

    public static final AutorDomain VACIO = AutorDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private AutorDomain() {}

    public static AutorDomain crear(UUID id, String identificador, String nombre, String email,
                                            Instant ocurridoEn) {
        var autor = new AutorDomain();
        var result = new ValidationResult();

        autor.setId(id, result);
        autor.setIdentificador(identificador, result);
        autor.setNombre(nombre, result);
        autor.setEmail(email, result);
        autor.setOcurridoEn(ocurridoEn, result);
        autor.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return autor;
    }

    public static AutorDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                  Instant ocurridoEn, Instant eliminadoEn) {
        var autor = new AutorDomain();
        autor.id = id;
        autor.identificador = identificador;
        autor.nombre = nombre;
        autor.email = email;
        autor.ocurridoEn = ocurridoEn;
        autor.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return autor;
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
                BibliotecaFields.Autor.ID,
                BibliotecaCodes.Autor.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                BibliotecaFields.Autor.IDENTIFICADOR,
                BibliotecaCodes.Autor.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                BibliotecaFields.Autor.NOMBRE,
                BibliotecaCodes.Autor.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                BibliotecaFields.Autor.EMAIL,
                BibliotecaCodes.Autor.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                BibliotecaFields.Autor.OCURRIDO_EN,
                BibliotecaCodes.Autor.OCURRIDO_EN_REQUERIDO, result)) {
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
