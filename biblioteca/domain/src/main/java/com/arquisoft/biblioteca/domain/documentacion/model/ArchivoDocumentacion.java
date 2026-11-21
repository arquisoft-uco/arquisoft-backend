package com.arquisoft.biblioteca.domain.documentacion.model;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ArchivoDocumentacion(
        UUID documentacion,
        EstadoDocumentacion estado,
        String claveObjeto,
        String nombreArchivo
) {

    public static final ArchivoDocumentacion VACIO = new ArchivoDocumentacion(
            UtilUUID.obtenerUUIDPorDefecto(), EstadoDocumentacion.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
