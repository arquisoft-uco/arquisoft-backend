package com.arquisoft.biblioteca.domain.proyectogrado;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = ProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String titulo;
    private Instant ocurridoEn;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID id, String titulo, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();

        proyecto.setId(id, result);
        proyecto.setTitulo(titulo, result);
        proyecto.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, String titulo, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.titulo = titulo;
        proyecto.ocurridoEn = ocurridoEn;
        return proyecto;
    }

    public void actualizar(String titulo, Instant ocurridoEn) {
        var result = new ValidationResult();

        setTitulo(titulo, result);
        setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                BibliotecaFields.ProyectoGrado.ID,
                BibliotecaCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setTitulo(String titulo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(titulo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                BibliotecaFields.ProyectoGrado.TITULO,
                BibliotecaCodes.ProyectoGrado.TITULO_REQUERIDO, result)) {
            return;
        }
        this.titulo = recortado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                BibliotecaFields.ProyectoGrado.OCURRIDO_EN,
                BibliotecaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
