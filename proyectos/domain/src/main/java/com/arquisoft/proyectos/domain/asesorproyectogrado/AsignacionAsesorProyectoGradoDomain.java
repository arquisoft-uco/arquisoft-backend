package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AsignacionAsesorProyectoGradoDomain {

    private AsesorProyectoGradoDomain asesorProyectoGrado;
    private UUID coordinador;

    private AsignacionAsesorProyectoGradoDomain() {}

    public static AsignacionAsesorProyectoGradoDomain crear(AsesorProyectoGradoDomain asesorProyectoGrado,
                                                            UUID coordinador) {
        var asignacion = new AsignacionAsesorProyectoGradoDomain();
        var result = new ValidationResult();

        asignacion.setAsesorProyectoGrado(asesorProyectoGrado, result);
        asignacion.setCoordinador(coordinador, result);

        result.lanzarSiTieneErrores();
        return asignacion;
    }

    private void setAsesorProyectoGrado(AsesorProyectoGradoDomain asesorProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorProyectoGrado,
                ProyectosFields.AsesorProyectoGrado.ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.asesorProyectoGrado = asesorProyectoGrado;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.AsesorProyectoGrado.COORDINADOR,
                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public AsesorProyectoGradoDomain getAsesorProyectoGrado() {
        return asesorProyectoGrado;
    }

    public UUID getCoordinador() {
        return coordinador;
    }

    public UUID getProyectoGrado() {
        return asesorProyectoGrado.getProyectoGrado();
    }

    public UUID getAsesor() {
        return asesorProyectoGrado.getAsesor();
    }
}
