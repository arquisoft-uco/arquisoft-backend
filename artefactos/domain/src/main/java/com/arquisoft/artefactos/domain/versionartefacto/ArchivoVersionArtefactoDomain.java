package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ArchivoVersionArtefactoDomain {

    private static final String SEPARADOR_VERSION = "-v";

    public static final ArchivoVersionArtefactoDomain VACIO = ArchivoVersionArtefactoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilNumero.CERO, UtilTexto.VACIO, UtilTexto.VACIO,
            false);

    private UUID asesor;
    private String uriArchivo;
    private int numeroVersion;
    private String nombreDocumento;
    private String nombreProyecto;
    private boolean proyectoFinalizado;

    private ArchivoVersionArtefactoDomain() {}

    public static ArchivoVersionArtefactoDomain reconstruir(UUID asesor, String uriArchivo, int numeroVersion,
                                                            String nombreDocumento, String nombreProyecto,
                                                            boolean proyectoFinalizado) {
        var archivo = new ArchivoVersionArtefactoDomain();
        archivo.asesor = asesor;
        archivo.uriArchivo = uriArchivo;
        archivo.numeroVersion = numeroVersion;
        archivo.nombreDocumento = nombreDocumento;
        archivo.nombreProyecto = nombreProyecto;
        archivo.proyectoFinalizado = proyectoFinalizado;
        return archivo;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public String getUriArchivo() {
        return uriArchivo;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public boolean isProyectoFinalizado() {
        return proyectoFinalizado;
    }

    public String getNombreRespaldo() {
        return nombreDocumento + SEPARADOR_VERSION + numeroVersion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
