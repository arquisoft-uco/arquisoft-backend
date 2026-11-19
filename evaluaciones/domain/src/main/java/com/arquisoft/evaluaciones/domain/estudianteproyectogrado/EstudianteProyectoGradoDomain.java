package com.arquisoft.evaluaciones.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstudianteProyectoGradoDomain {

    public static final EstudianteProyectoGradoDomain VACIO = EstudianteProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO, UtilTexto.VACIO, UtilFecha.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID estudiante;
    private UUID proyectoGrado;
    private String nombre;
    private String email;
    private Instant ocurridoEn;
    private Instant eliminadoEn;

    private EstudianteProyectoGradoDomain() {}

    public static EstudianteProyectoGradoDomain crear(UUID estudiante, UUID proyectoGrado, Instant ocurridoEn) {
        return crear(estudiante, UtilTexto.VACIO, UtilTexto.VACIO, proyectoGrado, ocurridoEn);
    }

    public static EstudianteProyectoGradoDomain crear(
            UUID estudiante, String nombre, String email, UUID proyectoGrado, Instant ocurridoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        var result = new ValidationResult();

        vinculo.setId();
        vinculo.setEstudiante(estudiante, result);
        vinculo.setContacto(nombre, email, result);
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setOcurridoEn(ocurridoEn, result);
        vinculo.setEliminadoEnInicial();

        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static EstudianteProyectoGradoDomain reconstruir(
            UUID id, UUID estudiante, UUID proyectoGrado, String nombre, String email,
            Instant ocurridoEn, Instant eliminadoEn) {
        var vinculo = new EstudianteProyectoGradoDomain();
        vinculo.id = id;
        vinculo.estudiante = estudiante;
        vinculo.proyectoGrado = proyectoGrado;
        vinculo.nombre = nombre;
        vinculo.email = email;
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

    public void refrescarContacto(Instant ocurridoEn, String nombre, String email) {
        var result = new ValidationResult();

        setOcurridoEn(ocurridoEn, result);
        setContacto(nombre, email, result);

        result.lanzarSiTieneErrores();
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

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE,
                EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    private void setContacto(String nombre, String email, ValidationResult result) {
        if (ValidatorObjeto.noNulo(nombre,
                EvaluacionesFields.EstudianteProyectoGrado.NOMBRE,
                EvaluacionesCodes.EstudianteProyectoGrado.NOMBRE_REQUERIDO, result)) {
            this.nombre = nombre;
        }
        if (ValidatorObjeto.noNulo(email,
                EvaluacionesFields.EstudianteProyectoGrado.EMAIL,
                EvaluacionesCodes.EstudianteProyectoGrado.EMAIL_REQUERIDO, result)) {
            this.email = email;
        }
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        if (UtilUUID.esPorDefecto(proyectoGrado)) {
            result.agregarError(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN,
                EvaluacionesCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
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

    public UUID getEstudiante() {
        return estudiante;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
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
