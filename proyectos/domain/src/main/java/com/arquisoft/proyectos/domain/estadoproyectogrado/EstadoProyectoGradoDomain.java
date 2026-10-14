package com.arquisoft.proyectos.domain.estadoproyectogrado;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstadoProyectoGradoDomain {

    public static final EstadoProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoProyecto.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private UUID proyectoGrado;
    private EstadoProyecto estadoProyecto;
    private Instant fechaActualizacion;

    private EstadoProyectoGradoDomain() {}

    public static EstadoProyectoGradoDomain crear(UUID proyectoGrado) {
        var estado = new EstadoProyectoGradoDomain();
        var result = new ValidationResult();

        estado.setId();
        estado.setProyectoGrado(proyectoGrado, result);
        estado.setEstadoProyectoInicial();
        estado.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return estado;
    }

    public static EstadoProyectoGradoDomain reconstruir(UUID id, UUID proyectoGrado, EstadoProyecto estadoProyecto,
                                                        Instant fechaActualizacion) {
        var estado = new EstadoProyectoGradoDomain();
        estado.id = id;
        estado.proyectoGrado = proyectoGrado;
        estado.estadoProyecto = estadoProyecto;
        estado.fechaActualizacion = fechaActualizacion;
        return estado;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstadoProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstadoProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoProyectoInicial() {
        this.estadoProyecto = EstadoProyecto.EN_PROCESO;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilFecha.generarInstanteActual();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
