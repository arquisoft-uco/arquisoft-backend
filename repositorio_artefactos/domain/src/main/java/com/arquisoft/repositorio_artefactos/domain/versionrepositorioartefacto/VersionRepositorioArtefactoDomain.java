package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosAlmacenamiento;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;

import java.time.Instant;
import java.util.UUID;

public final class VersionRepositorioArtefactoDomain {

    private UUID id;
    private String nombreDocumento;
    private int numeroVersion;
    private Instant fechaCreacion;
    private UUID repositorioArtefacto;
    private String uriArchivo;
    private String tipoContenido;
    private long tamanoBytes;
    private String nombreArchivo;
    private String hashSha256;

    private VersionRepositorioArtefactoDomain() {}

    public static VersionRepositorioArtefactoDomain crear(
            SubidaVersionRepositorioArtefactoDomain subida, int ultimoNumeroVersion,
            CargaPendienteDomain carga, AnalisisArchivoDomain analisis) {
        var version = new VersionRepositorioArtefactoDomain();

        version.setId();
        version.setRepositorioArtefacto(subida.getRepositorio());
        version.setNombreDocumento(subida.getNombreDocumento());
        version.setNumeroVersion(ultimoNumeroVersion);
        version.setFechaCreacion();
        version.setUriArchivo();
        version.setNombreArchivo(carga.getNombreArchivo());
        version.setTipoContenido(carga.getTipoContenido());
        version.setTamanoBytes(carga.getTamanoBytes());
        version.setHashSha256(analisis.getHashSha256());

        return version;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setRepositorioArtefacto(UUID repositorioArtefacto) {
        this.repositorioArtefacto = repositorioArtefacto;
    }

    private void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    private void setNumeroVersion(int ultimoNumeroVersion) {
        this.numeroVersion = ultimoNumeroVersion + 1;
    }

    private void setFechaCreacion() {
        this.fechaCreacion = UtilFecha.generarInstanteActual();
    }

    private void setUriArchivo() {
        this.uriArchivo = RepositorioArtefactosAlmacenamiento.PREFIJO_VERSIONES + id;
    }

    private void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    private void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    private void setTamanoBytes(long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    private void setHashSha256(String hashSha256) {
        this.hashSha256 = hashSha256;
    }

    public UUID getId() {
        return id;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public UUID getRepositorioArtefacto() {
        return repositorioArtefacto;
    }

    public String getUriArchivo() {
        return uriArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getHashSha256() {
        return hashSha256;
    }
}
