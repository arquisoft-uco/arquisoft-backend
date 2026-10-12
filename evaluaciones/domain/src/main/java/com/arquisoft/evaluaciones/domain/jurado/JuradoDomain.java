package com.arquisoft.evaluaciones.domain.jurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class JuradoDomain {

    public static final JuradoDomain VACIO = JuradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private JuradoDomain() {}

    public static JuradoDomain crear(UUID id, String identificador, String nombre, String email,
                                            Instant ocurridoEn) {
        var jurado = new JuradoDomain();
        var result = new ValidationResult();

        jurado.setId(id, result);
        jurado.setIdentificador(identificador, result);
        jurado.setNombre(nombre, result);
        jurado.setEmail(email, result);
        jurado.setOcurridoEn(ocurridoEn, result);
        jurado.setEliminadoEnInicial();

        result.lanzarSiTieneErrores();
        return jurado;
    }

    public static JuradoDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                                  Instant ocurridoEn, Instant eliminadoEn) {
        var jurado = new JuradoDomain();
        jurado.id = id;
        jurado.identificador = identificador;
        jurado.nombre = nombre;
        jurado.email = email;
        jurado.ocurridoEn = ocurridoEn;
        jurado.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return jurado;
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
        var result = new ValidationResult();

        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        this.eliminadoEn = ocurridoEn;
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EvaluacionesFields.Jurado.ID,
                EvaluacionesCodes.Jurado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                EvaluacionesFields.Jurado.IDENTIFICADOR,
                EvaluacionesCodes.Jurado.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                EvaluacionesFields.Jurado.NOMBRE,
                EvaluacionesCodes.Jurado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                EvaluacionesFields.Jurado.EMAIL,
                EvaluacionesCodes.Jurado.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setEliminadoEnInicial() {
        this.eliminadoEn = UtilFecha.VACIO;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EvaluacionesFields.Jurado.OCURRIDO_EN,
                EvaluacionesCodes.Jurado.OCURRIDO_EN_REQUERIDO, result)) {
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
