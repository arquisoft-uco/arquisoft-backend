package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionTareaDomain {

    private UUID tarea;
    private String descripcion;
    private UUID asesor;

    private ModificacionTareaDomain() {}

    public static ModificacionTareaDomain crear(UUID tarea, String descripcion, UUID asesor) {
        var modificacion = new ModificacionTareaDomain();
        var result = new ValidationResult();

        modificacion.setTarea(tarea, result);
        modificacion.setDescripcion(descripcion, result);
        modificacion.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.Tarea.DESCRIPCION_MAX,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                MapasRutaFields.Tarea.ASESOR,
                MapasRutaCodes.Tarea.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getTarea() {
        return tarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
