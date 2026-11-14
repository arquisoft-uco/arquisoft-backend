package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ActualizacionEstadoProyectoGradoDomain {

    private UUID proyectoGrado;
    private EstadoProyecto estadoProyecto;
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
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoProyecto(String estadoProyecto, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estadoProyecto);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.ProyectoGrado.ESTADO_PROYECTO,
                ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoProyecto.esValido(recortado)) {
            result.agregarError(
                    ArtefactosFields.ProyectoGrado.ESTADO_PROYECTO,
                    ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO,
                    Mensajes.obtener(ProyectoGradoKey.ERROR_ESTADO_NO_ENCONTRADO));
            return;
        }
        this.estadoProyecto = EstadoProyecto.desde(recortado);
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.ProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }
}
