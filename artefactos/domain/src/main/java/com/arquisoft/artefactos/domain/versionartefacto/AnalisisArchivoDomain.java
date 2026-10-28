package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.util.UtilTexto;

public final class AnalisisArchivoDomain {

    public static final AnalisisArchivoDomain VACIO =
            AnalisisArchivoDomain.reconstruir(UtilTexto.VACIO, UtilTexto.VACIO);

    private String tipoDetectado;
    private String etag;

    private AnalisisArchivoDomain() {}

    public static AnalisisArchivoDomain reconstruir(String tipoDetectado, String etag) {
        var analisis = new AnalisisArchivoDomain();
        analisis.tipoDetectado = tipoDetectado;
        analisis.etag = etag;
        return analisis;
    }

    public String getTipoDetectado() {
        return tipoDetectado;
    }

    public String getEtag() {
        return etag;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
