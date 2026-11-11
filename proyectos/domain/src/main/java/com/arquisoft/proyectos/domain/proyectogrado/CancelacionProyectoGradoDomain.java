package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CancelacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private UUID coordinador;
    private String motivo;

    private CancelacionProyectoGradoDomain() {}

    public static CancelacionProyectoGradoDomain crear(UUID proyectoGrado, UUID coordinador, String motivo) {
        var cancelacion = new CancelacionProyectoGradoDomain();
        var result = new ValidationResult();

        cancelacion.setProyectoGrado(proyectoGrado, result);
        cancelacion.setCoordinador(coordinador, result);
        cancelacion.setMotivo(motivo, result);

        result.lanzarSiTieneErrores();
        return cancelacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.ProyectoGrado.COORDINADOR,
                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    private void setMotivo(String motivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(motivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ProyectosFields.ProyectoGrado.MOTIVO,
                ProyectosCodes.ProyectoGrado.MOTIVO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, ProyectosLimits.ProyectoGrado.MOTIVO_MAX,
                ProyectosFields.ProyectoGrado.MOTIVO,
                ProyectosCodes.ProyectoGrado.MOTIVO_LONGITUD_MAXIMA, result)) {
            return;
        }
        this.motivo = recortado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getCoordinador() {
        return coordinador;
    }

    public String getMotivo() {
        return motivo;
    }
}
