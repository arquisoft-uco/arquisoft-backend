package com.arquisoft.shared.webclient;

import java.util.Map;

public record SolicitudHttp(
        MetodoHttp metodo,
        String ruta,
        Map<String, String> cabeceras,
        Object cuerpo,
        TipoContenido tipoContenido) {

    public SolicitudHttp {
        cabeceras = cabeceras == null ? Map.of() : Map.copyOf(cabeceras);
    }
}
