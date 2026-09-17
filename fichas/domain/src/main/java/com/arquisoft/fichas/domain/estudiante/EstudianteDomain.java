package com.arquisoft.fichas.domain.estudiante;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteDomain {

    public static final EstudianteDomain VACIO = EstudianteDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private EstudianteDomain() {}

    public static EstudianteDomain crear(UUID id, String identificador, String nombre, String email,
                                          Instant ocurridoEn) {
        var estudiante = new EstudianteDomain();
        var result = new ValidationResult();

        estudiante.setId(id, result);
        estudiante.setIdentificador(identificador, result);
        estudiante.setNombre(nombre, result);
        estudiante.setEmail(email, result);
        estudiante.setOcurridoEn(ocurridoEn, result);
        estudiante.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return estudiante;
    }

    public static EstudianteDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                Instant ocurridoEn, Instant eliminadoEn) {
        var estudiante = new EstudianteDomain();
        estudiante.id = id;
        estudiante.identificador = identificador;
        estudiante.nombre = nombre;
        estudiante.email = email;
        estudiante.ocurridoEn = ocurridoEn;
        estudiante.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return estudiante;
    }

    public void remover(Instant ocurridoEn) {
        this.eliminadoEn = ocurridoEn;
        this.ocurridoEn = ocurridoEn;
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

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                FichasFields.Estudiante.ID,
                FichasCodes.Estudiante.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                FichasFields.Estudiante.IDENTIFICADOR,
                FichasCodes.Estudiante.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                FichasFields.Estudiante.NOMBRE,
                FichasCodes.Estudiante.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                FichasFields.Estudiante.EMAIL,
                FichasCodes.Estudiante.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                FichasFields.Estudiante.OCURRIDO_EN,
                FichasCodes.Estudiante.OCURRIDO_EN_REQUERIDO, result)) {
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
