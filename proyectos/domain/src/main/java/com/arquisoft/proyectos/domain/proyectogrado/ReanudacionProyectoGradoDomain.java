package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.LocalDate;
import java.util.UUID;

public final class ReanudacionProyectoGradoDomain {

    private UUID proyectoGrado;
    private LocalDate fechaFin;
    private LocalDate hoy;

    private ReanudacionProyectoGradoDomain() {}

    public static ReanudacionProyectoGradoDomain crear(UUID proyectoGrado, LocalDate fechaFin, LocalDate hoy) {
        var reanudacion = new ReanudacionProyectoGradoDomain();
        var result = new ValidationResult();

        reanudacion.setProyectoGrado(proyectoGrado, result);
        reanudacion.setFechaFin(fechaFin, result);
        reanudacion.setHoy(hoy, result);

        result.lanzarSiTieneErrores();
        return reanudacion;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setFechaFin(LocalDate fechaFin, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fechaFin,
                ProyectosFields.ProyectoGrado.FECHA_FIN,
                ProyectosCodes.ProyectoGrado.FECHA_FIN_REQUERIDA, result)) {
            return;
        }
        this.fechaFin = fechaFin;
    }

    private void setHoy(LocalDate hoy, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(hoy,
                ProyectosFields.ProyectoGrado.HOY,
                ProyectosCodes.ProyectoGrado.HOY_REQUERIDA, result)) {
            return;
        }
        this.hoy = hoy;
    }

    public boolean plazoVigente() {
        return !fechaFin.isBefore(hoy);
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public LocalDate getHoy() {
        return hoy;
    }
}
