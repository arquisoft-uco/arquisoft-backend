package com.arquisoft.fichas.domain.representantecomite;

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

public final class RepresentanteComiteDomain {

    public static final RepresentanteComiteDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO, Instant.EPOCH,
            UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private RepresentanteComiteDomain() {}

    public static RepresentanteComiteDomain crear(UUID id, String identificador, String nombre, String email,
                                                  Instant ocurridoEn) {
        var representanteComite = new RepresentanteComiteDomain();
        var result = new ValidationResult();

        representanteComite.setId(id, result);
        representanteComite.setIdentificador(identificador, result);
        representanteComite.setNombre(nombre, result);
        representanteComite.setEmail(email, result);
        representanteComite.setOcurridoEn(ocurridoEn, result);
        representanteComite.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return representanteComite;
    }

    public static RepresentanteComiteDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                        Instant ocurridoEn, Instant eliminadoEn) {
        var representanteComite = new RepresentanteComiteDomain();
        representanteComite.id = id;
        representanteComite.identificador = identificador;
        representanteComite.nombre = nombre;
        representanteComite.email = email;
        representanteComite.ocurridoEn = ocurridoEn;
        representanteComite.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return representanteComite;
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

    public void actualizar(String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        setIdentificador(identificador, result);
        setNombre(nombre, result);
        setEmail(email, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                FichasFields.RepresentanteComite.ID,
                FichasCodes.RepresentanteComite.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(identificador);
        if (!ValidatorTexto.noEnBlanco(recortado,
                FichasFields.RepresentanteComite.IDENTIFICADOR,
                FichasCodes.RepresentanteComite.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = recortado;
    }

    private void setNombre(String nombre, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(recortado,
                FichasFields.RepresentanteComite.NOMBRE,
                FichasCodes.RepresentanteComite.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = recortado;
    }

    private void setEmail(String email, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(email);
        if (!ValidatorTexto.noEnBlanco(recortado,
                FichasFields.RepresentanteComite.EMAIL,
                FichasCodes.RepresentanteComite.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = recortado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                FichasFields.RepresentanteComite.OCURRIDO_EN,
                FichasCodes.RepresentanteComite.OCURRIDO_EN_REQUERIDO, result)) {
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
