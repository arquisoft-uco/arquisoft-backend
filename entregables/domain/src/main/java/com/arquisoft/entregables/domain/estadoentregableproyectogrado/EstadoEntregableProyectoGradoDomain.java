package com.arquisoft.entregables.domain.estadoentregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class EstadoEntregableProyectoGradoDomain {

    public static final EstadoEntregableProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoEntregable.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private UUID entregableProyectoGrado;
    private EstadoEntregable estadoEntregable;
    private Instant fechaActualizacion;

    private EstadoEntregableProyectoGradoDomain() {}

    public static EstadoEntregableProyectoGradoDomain crear(UUID entregableProyectoGrado) {
        var estado = new EstadoEntregableProyectoGradoDomain();
        var result = new ValidationResult();

        estado.setId();
        estado.setEntregableProyectoGrado(entregableProyectoGrado, result);
        estado.setEstadoEntregableInicial();
        estado.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return estado;
    }

    public static EstadoEntregableProyectoGradoDomain crearConEstado(UUID entregableProyectoGrado,
                                                                     EstadoEntregable estadoEntregable) {
        var estado = new EstadoEntregableProyectoGradoDomain();
        var result = new ValidationResult();

        estado.setId();
        estado.setEntregableProyectoGrado(entregableProyectoGrado, result);
        estado.setEstadoEntregable(estadoEntregable, result);
        estado.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return estado;
    }

    public static EstadoEntregableProyectoGradoDomain reconstruir(UUID id, UUID entregableProyectoGrado,
                                                                  EstadoEntregable estadoEntregable,
                                                                  Instant fechaActualizacion) {
        var estado = new EstadoEntregableProyectoGradoDomain();
        estado.id = id;
        estado.entregableProyectoGrado = entregableProyectoGrado;
        estado.estadoEntregable = estadoEntregable;
        estado.fechaActualizacion = fechaActualizacion;
        return estado;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.EstadoEntregableProyectoGrado.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.EstadoEntregableProyectoGrado.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setEstadoEntregable(EstadoEntregable estadoEntregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estadoEntregable,
                EntregablesFields.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE,
                EntregablesCodes.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        if (estadoEntregable == EstadoEntregable.VACIO) {
            result.agregarError(
                    EntregablesFields.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE,
                    EntregablesCodes.EstadoEntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO,
                    Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO));
            return;
        }
        this.estadoEntregable = estadoEntregable;
    }

    private void setEstadoEntregableInicial() {
        this.estadoEntregable = EstadoEntregable.NUEVO;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilFecha.generarInstanteActual();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
