package com.arquisoft.solicitudes.domain.tiposolicitud;

import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

import java.util.Optional;

public enum TipoSolicitud {

    NOVEDAD_PARA_EL_COORDINADOR("Novedad para el Coordinador"),
    NOVEDAD_PARA_EL_ASESOR("Novedad para el Asesor"),
    CAMBIO_DE_ASESOR("Cambio de Asesor"),
    AMPLIACION_DE_PLAZO("Ampliación de Plazo"),
    REGISTRO_Y_MODIFICACION_DE_USUARIOS("Registro y modificación de Usuarios"),

    VACIO("");

    private final String id;
    private final String nombre;

    TipoSolicitud(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoSolicitud desde(String id) {
        return delCatalogo(id).orElseThrow(() -> new TipoSolicitudNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return delCatalogo(id).isPresent();
    }

    private static Optional<TipoSolicitud> delCatalogo(String id) {
        return UtilEnum.desde(TipoSolicitud.class, id).filter(tipo -> tipo != VACIO);
    }
}
