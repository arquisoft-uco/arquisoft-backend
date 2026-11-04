package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.util.UtilTexto;

public final class AnalisisArchivoDomain {

    public static final AnalisisArchivoDomain VACIO =
            AnalisisArchivoDomain.reconstruir(UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);

    private String tipoDetectado;
    private String hashSha256;
    private String etag;

    private AnalisisArchivoDomain() {}

    public static AnalisisArchivoDomain reconstruir(String tipoDetectado, String hashSha256, String etag) {
        var analisis = new AnalisisArchivoDomain();
        analisis.tipoDetectado = tipoDetectado;
        analisis.hashSha256 = hashSha256;
        analisis.etag = etag;
        return analisis;
    }

    public String getTipoDetectado() {
        return tipoDetectado;
    }

    public String getHashSha256() {
        return hashSha256;
    }

    public String getEtag() {
        return etag;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
