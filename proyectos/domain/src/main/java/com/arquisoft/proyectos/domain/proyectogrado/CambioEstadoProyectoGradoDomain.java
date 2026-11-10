package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoKey;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoProyectoGradoDomain {

    private UUID proyectoGrado;
    private UUID asesor;
    private EstadoProyecto estadoNuevo;

    private CambioEstadoProyectoGradoDomain() {}

    public static CambioEstadoProyectoGradoDomain crear(UUID proyectoGrado, UUID asesor, String estadoProyecto) {
        var cambio = new CambioEstadoProyectoGradoDomain();
        var result = new ValidationResult();

        cambio.setProyectoGrado(proyectoGrado, result);
        cambio.setAsesor(asesor, result);
        cambio.setEstadoNuevo(estadoProyecto, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ProyectosFields.ProyectoGrado.ASESOR,
                ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEstadoNuevo(String estadoProyecto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(estadoProyecto,
                ProyectosFields.ProyectoGrado.ESTADO_PROYECTO,
                ProyectosCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoProyecto.esValido(estadoProyecto)) {
            result.agregarError(
                    ProyectosFields.ProyectoGrado.ESTADO_PROYECTO,
                    ProyectosCodes.ProyectoGrado.ESTADO_PROYECTO_INVALIDO,
                    Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO));
            return;
        }
        this.estadoNuevo = EstadoProyecto.desde(estadoProyecto);
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public EstadoProyecto getEstadoNuevo() {
        return estadoNuevo;
    }
}
