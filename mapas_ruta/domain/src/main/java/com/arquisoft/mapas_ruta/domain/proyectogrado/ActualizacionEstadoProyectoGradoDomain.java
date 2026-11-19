package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ActualizacionEstadoProyectoGradoDomain {

    private UUID proyectoGrado;
    private EstadoProyectoGrado estadoProyecto;
    private Instant ocurridoEn;

    private ActualizacionEstadoProyectoGradoDomain() {}

    public static ActualizacionEstadoProyectoGradoDomain crear(
            UUID proyectoGrado, String estadoProyecto, Instant ocurridoEn) {
        var actualizacion = new ActualizacionEstadoProyectoGradoDomain();
        var result = new ValidationResult();

        actualizacion.setProyectoGrado(proyectoGrado, result);
        actualizacion.setEstadoProyecto(estadoProyecto, result);
        actualizacion.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return actualizacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                MapasRutaFields.ProyectoGrado.ID,
                MapasRutaCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoProyecto(String estadoProyecto, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estadoProyecto);
        if (!ValidatorTexto.noEnBlanco(recortado,
                MapasRutaFields.ProyectoGrado.ESTADO_PROYECTO,
                MapasRutaCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoProyectoGrado.esValido(recortado)) {
            result.agregarError(
                    MapasRutaFields.ProyectoGrado.ESTADO_PROYECTO,
                    MapasRutaCodes.ProyectoGrado.ESTADO_NO_ENCONTRADO,
                    Mensajes.formatear(ProyectoGradoMapasRutaKey.ERROR_ESTADO_NO_ENCONTRADO));
            return;
        }
        this.estadoProyecto = EstadoProyectoGrado.desde(recortado);
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                MapasRutaFields.ProyectoGrado.OCURRIDO_EN,
                MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoProyectoGrado getEstadoProyecto() {
        return estadoProyecto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }
}
