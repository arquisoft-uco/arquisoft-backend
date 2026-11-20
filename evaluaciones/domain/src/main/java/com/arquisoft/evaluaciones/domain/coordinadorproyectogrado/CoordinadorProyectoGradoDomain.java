package com.arquisoft.evaluaciones.domain.coordinadorproyectogrado;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class CoordinadorProyectoGradoDomain {

    public static final CoordinadorProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO,
            UtilFecha.VACIO);

    private UUID proyectoGrado;
    private UUID coordinador;
    private String nombre;
    private String email;
    private Instant ocurridoEn;

    private CoordinadorProyectoGradoDomain() {}

    public static CoordinadorProyectoGradoDomain crear(
            UUID proyectoGrado, UUID coordinador, String nombre, String email, Instant ocurridoEn) {
        var replica = new CoordinadorProyectoGradoDomain();
        var result = new ValidationResult();

        replica.setProyectoGrado(proyectoGrado, result);
        replica.setCoordinador(coordinador, result);
        replica.setNombre(nombre, result);
        replica.setEmail(email, result);
        replica.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return replica;
    }

    public static CoordinadorProyectoGradoDomain reconstruir(
            UUID proyectoGrado, UUID coordinador, String nombre, String email, Instant ocurridoEn) {
        var replica = new CoordinadorProyectoGradoDomain();
        replica.proyectoGrado = proyectoGrado;
        replica.coordinador = coordinador;
        replica.nombre = nombre;
        replica.email = email;
        replica.ocurridoEn = ocurridoEn;
        return replica;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                EvaluacionesCodes.CoordinadorProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        if (UtilUUID.esPorDefecto(proyectoGrado)) {
            result.agregarError(EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                    EvaluacionesCodes.CoordinadorProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                EvaluacionesFields.CoordinadorProyectoGrado.COORDINADOR,
                EvaluacionesCodes.CoordinadorProyectoGrado.COORDINADOR_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(nombre,
                EvaluacionesFields.CoordinadorProyectoGrado.NOMBRE,
                EvaluacionesCodes.CoordinadorProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(email,
                EvaluacionesFields.CoordinadorProyectoGrado.EMAIL,
                EvaluacionesCodes.CoordinadorProyectoGrado.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EvaluacionesFields.CoordinadorProyectoGrado.OCURRIDO_EN,
                EvaluacionesCodes.CoordinadorProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getCoordinador() {
        return coordinador;
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
}
