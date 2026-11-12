package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.shared.message.constant.EntregablesAlmacenamiento;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ArtefactoDomain {

    public static final ArtefactoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO,
            new VersionCerrada(UtilUUID.obtenerUUIDPorDefecto(), UtilNumero.CERO, UtilFecha.VACIO),
            new DatosRepositorio(UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO),
            new Omision(null, null));

    private UUID id;
    private UUID proyectoGrado;
    private String nombreDocumento;
    private String claveObjeto;
    private UUID versionArtefacto;
    private int numeroVersion;
    private Instant fechaVersion;
    private UUID versionRepositorioArtefacto;
    private UUID repositorio;
    private String nombreRepositorio;
    private Instant omitidoEn;
    private Instant omisionOcurridaEn;

    private ArtefactoDomain() {}

    public record VersionCerrada(UUID versionArtefacto, int numeroVersion, Instant fechaVersion) {}

    public record DatosRepositorio(UUID versionRepositorioArtefacto, UUID repositorio, String nombreRepositorio) {}

    public record Omision(Instant omitidoEn, Instant omisionOcurridaEn) {}

    public static ArtefactoDomain crear(UUID id, UUID proyectoGrado, String nombreDocumento, VersionCerrada version,
                                        DatosRepositorio datosRepositorio) {
        var artefacto = new ArtefactoDomain();
        var result = new ValidationResult();
        artefacto.setId(id, result);
        artefacto.setProyectoGrado(proyectoGrado, result);
        artefacto.setNombreDocumento(nombreDocumento, result);
        artefacto.setVersionArtefacto(version.versionArtefacto(), result);
        artefacto.setNumeroVersion(version.numeroVersion(), result);
        artefacto.setFechaVersion(version.fechaVersion(), result);
        artefacto.setDatosRepositorio(datosRepositorio, result);
        artefacto.setClaveObjeto();
        result.lanzarSiTieneErrores();
        return artefacto;
    }

    public static ArtefactoDomain reconstruir(UUID id, UUID proyectoGrado, String nombreDocumento, String claveObjeto,
                                              VersionCerrada version, DatosRepositorio datosRepositorio,
                                              Omision omision) {
        var artefacto = new ArtefactoDomain();
        artefacto.id = id;
        artefacto.proyectoGrado = proyectoGrado;
        artefacto.nombreDocumento = nombreDocumento;
        artefacto.claveObjeto = claveObjeto;
        artefacto.versionArtefacto = version.versionArtefacto();
        artefacto.numeroVersion = version.numeroVersion();
        artefacto.fechaVersion = version.fechaVersion();
        artefacto.versionRepositorioArtefacto = datosRepositorio.versionRepositorioArtefacto();
        artefacto.repositorio = datosRepositorio.repositorio();
        artefacto.nombreRepositorio = datosRepositorio.nombreRepositorio();
        artefacto.omitidoEn = omision.omitidoEn();
        artefacto.omisionOcurridaEn = omision.omisionOcurridaEn();
        return artefacto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EntregablesFields.Artefacto.ID,
                EntregablesCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.Artefacto.PROYECTO_GRADO,
                EntregablesCodes.Artefacto.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setNombreDocumento(String nombreDocumento, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombreDocumento,
                EntregablesFields.Artefacto.NOMBRE_DOCUMENTO,
                EntregablesCodes.Artefacto.NOMBRE_DOCUMENTO_REQUERIDO, result)) {
            return;
        }
        this.nombreDocumento = nombreDocumento;
    }

    private void setVersionArtefacto(UUID versionArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionArtefacto,
                EntregablesFields.Artefacto.VERSION_ARTEFACTO,
                EntregablesCodes.Artefacto.VERSION_ARTEFACTO_INVALIDA, result)) {
            return;
        }
        this.versionArtefacto = versionArtefacto;
    }

    private void setNumeroVersion(int numeroVersion, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(numeroVersion, EntregablesLimits.Artefacto.NUMERO_VERSION_MIN,
                EntregablesFields.Artefacto.NUMERO_VERSION,
                EntregablesCodes.Artefacto.NUMERO_VERSION_INVALIDO, result)) {
            return;
        }
        this.numeroVersion = numeroVersion;
    }

    private void setFechaVersion(Instant fechaVersion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fechaVersion,
                EntregablesFields.Artefacto.FECHA_VERSION,
                EntregablesCodes.Artefacto.FECHA_VERSION_REQUERIDA, result)) {
            return;
        }
        this.fechaVersion = fechaVersion;
    }

    private void setDatosRepositorio(DatosRepositorio datosRepositorio, ValidationResult result) {
        if (ValidatorObjeto.noNulo(datosRepositorio.versionRepositorioArtefacto(),
                EntregablesFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                EntregablesCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_INVALIDA, result)) {
            this.versionRepositorioArtefacto = datosRepositorio.versionRepositorioArtefacto();
        }
        if (ValidatorObjeto.noNulo(datosRepositorio.repositorio(),
                EntregablesFields.Artefacto.REPOSITORIO,
                EntregablesCodes.Artefacto.REPOSITORIO_INVALIDO, result)) {
            this.repositorio = datosRepositorio.repositorio();
        }
        if (ValidatorTexto.noEnBlanco(datosRepositorio.nombreRepositorio(),
                EntregablesFields.Artefacto.NOMBRE_REPOSITORIO,
                EntregablesCodes.Artefacto.NOMBRE_REPOSITORIO_REQUERIDO, result)) {
            this.nombreRepositorio = datosRepositorio.nombreRepositorio();
        }
    }

    private void setClaveObjeto() {
        this.claveObjeto = EntregablesAlmacenamiento.PREFIJO_ARTEFACTOS + id + "/v" + numeroVersion;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public UUID getVersionArtefacto() {
        return versionArtefacto;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public Instant getFechaVersion() {
        return fechaVersion;
    }

    public UUID getVersionRepositorioArtefacto() {
        return versionRepositorioArtefacto;
    }

    public UUID getRepositorio() {
        return repositorio;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }

    public Instant getOmitidoEn() {
        return omitidoEn;
    }

    public Instant getOmisionOcurridaEn() {
        return omisionOcurridaEn;
    }

    public boolean estaOmitido() {
        return UtilObjeto.noEsNulo(omitidoEn);
    }

    public boolean tieneOmisionIgualOPosteriorA(Instant ocurridoEn) {
        return UtilObjeto.noEsNulo(omisionOcurridaEn) && !omisionOcurridaEn.isBefore(ocurridoEn);
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
