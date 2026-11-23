package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import java.util.Arrays;

public enum EstadoEntregablePersistencia {

    NUEVO("NUEVO", "Nuevo"),
    DISPONIBLE_EVALUACION("DISPONIBLE_EVALUACION", "Disponible Evaluacion"),
    EN_EVALUACION("EN_EVALUACION", "En Evaluacion"),
    APROBADO_CON_OBSERVACIONES("APROBADO_CON_OBSERVACIONES", "Aprobado Con Observaciones"),
    APROBADO("APROBADO", "Aprobado"),
    RECHAZADO("RECHAZADO", "Rechazado"),
    CANCELADO("CANCELADO", "Cancelado"),
    CERRADO("CERRADO", "Cerrado");

    private static final String MENSAJE_ID_DESCONOCIDO = "Estado de entregable desconocido en la réplica: ";

    private final String id;
    private final String nombre;

    EstadoEntregablePersistencia(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static String nombreDe(String id) {
        return Arrays.stream(values())
                .filter(estado -> estado.id.equals(id))
                .findFirst()
                .map(EstadoEntregablePersistencia::getNombre)
                .orElseThrow(() -> new IllegalStateException(MENSAJE_ID_DESCONOCIDO + id));
    }
}
