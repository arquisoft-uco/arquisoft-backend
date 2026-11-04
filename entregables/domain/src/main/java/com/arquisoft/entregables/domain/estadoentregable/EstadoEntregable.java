package com.arquisoft.entregables.domain.estadoentregable;

import com.arquisoft.entregables.domain.estadoentregable.exception.EstadoEntregableNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum EstadoEntregable {

    DISPONIBLE_EVALUACION("Disponible Evaluacion"),
    EN_EVALUACION("En Evaluacion"),
    APROBADO_CON_OBSERVACIONES("Aprobado Con Observaciones"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    CANCELADO("Cancelado"),
    CERRADO("Cerrado"),

    VACIO("");

    private final String id;
    private final String nombre;

    EstadoEntregable(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static EstadoEntregable desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new EstadoEntregableNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<EstadoEntregable> delCatalogo(String id) {
        return UtilEnum.desde(EstadoEntregable.class, id).filter(estado -> estado != VACIO);
    }
}
