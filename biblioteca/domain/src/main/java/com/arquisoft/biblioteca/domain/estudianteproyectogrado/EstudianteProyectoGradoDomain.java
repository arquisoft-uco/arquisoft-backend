package com.arquisoft.biblioteca.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteProyectoGradoDomain {

    public static final EstudianteProyectoGradoDomain VACIO = EstudianteProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID autor;
    private UUID proyectoGrado;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private EstudianteProyectoGradoDomain() {}

    public static EstudianteProyectoGradoDomain crear(UUID autor, UUID proyectoGrado, Instant ocurridoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        var result = new ValidationResult();

        vinculo.setId();
        vinculo.setAutor(autor, result);
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setOcurridoEn(ocurridoEn, result);
        vinculo.setEliminadoEnInicial();

        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static EstudianteProyectoGradoDomain reconstruir(
            UUID id, UUID autor, UUID proyectoGrado, Instant ocurridoEn, Instant eliminadoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        vinculo.id = id;
        vinculo.autor = autor;
        vinculo.proyectoGrado = proyectoGrado;
        vinculo.ocurridoEn = ocurridoEn;
        vinculo.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return vinculo;
    }

    public void reactivar(Instant ocurridoEn) {
        var result = new ValidationResult();

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

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setAutor(UUID autor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(autor,
                BibliotecaFields.EstudianteProyectoGrado.AUTOR,
                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO, result)) {
            return;
        }
        this.autor = autor;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        if (UtilUUID.esPorDefecto(proyectoGrado)) {
            result.agregarError(BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                BibliotecaFields.EstudianteProyectoGrado.OCURRIDO_EN,
                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    private void setEliminadoEnInicial() {
        this.eliminadoEn = UtilFecha.VACIO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAutor() {
        return autor;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
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
