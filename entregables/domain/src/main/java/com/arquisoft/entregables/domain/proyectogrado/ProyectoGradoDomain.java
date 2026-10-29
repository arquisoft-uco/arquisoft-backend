package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ProyectoGradoDomain {

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

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombre(String nombre, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(recortado,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                EntregablesLimits.ProyectoGrado.NOMBRE_MAX,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_LONGITUD_MAXIMA, result)) {
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
}
