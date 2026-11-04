package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.util.UtilTexto;

public final class ArchivoVersionDomain {

    public static final ArchivoVersionDomain VACIO = ArchivoVersionDomain.reconstruir(UtilTexto.VACIO, UtilTexto.VACIO);

    private String uriArchivo;
    private String nombreArchivo;

    private ArchivoVersionDomain() {}

    public static ArchivoVersionDomain reconstruir(String uriArchivo, String nombreArchivo) {
        var archivo = new ArchivoVersionDomain();
        archivo.uriArchivo = uriArchivo;
        archivo.nombreArchivo = nombreArchivo;
        return archivo;
    }

    public String getUriArchivo() {
        return uriArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
