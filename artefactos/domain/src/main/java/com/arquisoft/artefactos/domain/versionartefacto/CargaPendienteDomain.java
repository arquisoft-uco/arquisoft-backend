package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class CargaPendienteDomain {

    public static final CargaPendienteDomain VACIO = CargaPendienteDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO, UtilTexto.VACIO, UtilNumero.CERO, UtilTexto.VACIO);

    private UUID carga;
    private UUID artefacto;
    private UUID solicitante;
    private String nombreArchivo;
    private String tipoContenido;
    private long tamanoBytes;
    private String etag;

    private CargaPendienteDomain() {}

    public static CargaPendienteDomain reconstruir(UUID carga, UUID artefacto, UUID solicitante,
                                                   String nombreArchivo, String tipoContenido,
                                                   long tamanoBytes, String etag) {
        var pendiente = new CargaPendienteDomain();
        pendiente.carga = carga;
        pendiente.artefacto = artefacto;
        pendiente.solicitante = solicitante;
        pendiente.nombreArchivo = nombreArchivo;
        pendiente.tipoContenido = tipoContenido;
        pendiente.tamanoBytes = tamanoBytes;
        pendiente.etag = etag;
        return pendiente;
    }

    public UUID getCarga() {
        return carga;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getSolicitante() {
        return solicitante;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public String getEtag() {
        return etag;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
