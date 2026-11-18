package com.arquisoft.proyectos.domain.estadoproyectogrado;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.key.proyectos.EstadoProyectoKey;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class EstadoProyectoGradoDomain {

    public static final EstadoProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoProyecto.VACIO,
            UtilFecha.VACIO,
            UtilTexto.VACIO);

    private UUID id;
    private UUID proyectoGrado;
    private EstadoProyecto estadoProyecto;
    private Instant fechaActualizacion;
    private String motivo = UtilTexto.VACIO;

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

    public static EstadoProyectoGradoDomain crearConEstado(UUID proyectoGrado, EstadoProyecto estadoProyecto) {
        var estado = new EstadoProyectoGradoDomain();
        var result = new ValidationResult();

        estado.setId();
        estado.setProyectoGrado(proyectoGrado, result);
        estado.setEstadoProyecto(estadoProyecto, result);
        estado.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return estado;
    }

    public static EstadoProyectoGradoDomain crearCancelado(UUID proyectoGrado, String motivo) {
        var estado = new EstadoProyectoGradoDomain();
        var result = new ValidationResult();

        estado.setId();
        estado.setProyectoGrado(proyectoGrado, result);
        estado.setEstadoProyectoCancelado();
        estado.setFechaActualizacion();
        estado.setMotivo(motivo, result);

        result.lanzarSiTieneErrores();
        return estado;
    }

    public static EstadoProyectoGradoDomain reconstruir(UUID id, UUID proyectoGrado, EstadoProyecto estadoProyecto,
                                                        Instant fechaActualizacion, String motivo) {
        var estado = new EstadoProyectoGradoDomain();
        estado.id = id;
        estado.proyectoGrado = proyectoGrado;
        estado.estadoProyecto = estadoProyecto;
        estado.fechaActualizacion = fechaActualizacion;
        estado.motivo = motivo;
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

    private void setEstadoProyecto(EstadoProyecto estadoProyecto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estadoProyecto,
                ProyectosFields.EstadoProyectoGrado.ESTADO_PROYECTO,
                ProyectosCodes.EstadoProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result)) {
            return;
        }
        if (estadoProyecto == EstadoProyecto.VACIO) {
            result.agregarError(
                    ProyectosFields.EstadoProyectoGrado.ESTADO_PROYECTO,
                    ProyectosCodes.EstadoProyectoGrado.ESTADO_PROYECTO_REQUERIDO,
                    Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO));
            return;
        }
        this.estadoProyecto = estadoProyecto;
    }

    private void setEstadoProyectoInicial() {
        this.estadoProyecto = EstadoProyecto.EN_PROCESO;
    }

    private void setEstadoProyectoCancelado() {
        this.estadoProyecto = EstadoProyecto.CANCELADO;
    }

    private void setMotivo(String motivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(motivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ProyectosFields.EstadoProyectoGrado.MOTIVO,
                ProyectosCodes.EstadoProyectoGrado.MOTIVO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, ProyectosLimits.ProyectoGrado.MOTIVO_MAX,
                ProyectosFields.EstadoProyectoGrado.MOTIVO,
                ProyectosCodes.EstadoProyectoGrado.MOTIVO_LONGITUD_MAXIMA, result)) {
            return;
        }
        this.motivo = recortado;
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

    public String getMotivo() {
        return motivo;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
