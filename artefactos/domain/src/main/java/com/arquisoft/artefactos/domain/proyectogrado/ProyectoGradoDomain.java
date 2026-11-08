package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO);

    private UUID id;
    private String nombre;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID id, String nombre) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();
        proyecto.setId(id, result);
        proyecto.setNombre(nombre, result);
        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, String nombre) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.nombre = nombre;
        return proyecto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombre(String nombre, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.ProyectoGrado.NOMBRE,
                ArtefactosCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.ProyectoGrado.NOMBRE_MAX,
                ArtefactosFields.ProyectoGrado.NOMBRE,
                ArtefactosCodes.ProyectoGrado.NOMBRE_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombre = recortado;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
