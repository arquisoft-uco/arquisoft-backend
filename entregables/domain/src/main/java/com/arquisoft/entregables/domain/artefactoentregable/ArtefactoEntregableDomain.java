package com.arquisoft.entregables.domain.artefactoentregable;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ArtefactoEntregableDomain {

    private UUID id;
    private UUID versionArtefacto;
    private UUID entregableProyectoGrado;
    private String uriArchivo;
    private UUID artefacto;
    private int numeroVersion;
    private UUID versionRepositorioArtefacto;
    private String nombreDocumento;
    private UUID repositorio;
    private String nombreRepositorio;

    private ArtefactoEntregableDomain() {}

    public record DatosRepositorioArtefacto(UUID versionRepositorioArtefacto, UUID repositorio, String nombreRepositorio) {}

    public static ArtefactoEntregableDomain crear(UUID entregableProyectoGrado, String uriArchivo, UUID artefacto,
                                                  int numeroVersion, String nombreDocumento,
                                                  DatosRepositorioArtefacto datosRepositorio) {
        var snapshot = new ArtefactoEntregableDomain();
        var result = new ValidationResult();
        snapshot.setId();
        snapshot.setVersionArtefacto();
        snapshot.setEntregableProyectoGrado(entregableProyectoGrado, result);
        snapshot.setUriArchivo(uriArchivo, result);
        snapshot.setArtefacto(artefacto, result);
        snapshot.setNumeroVersion(numeroVersion, result);
        snapshot.setNombreDocumento(nombreDocumento, result);
        snapshot.setVersionRepositorioArtefacto(datosRepositorio.versionRepositorioArtefacto(), result);
        snapshot.setRepositorio(datosRepositorio.repositorio(), result);
        snapshot.setNombreRepositorio(datosRepositorio.nombreRepositorio(), result);
        result.lanzarSiTieneErrores();
        return snapshot;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setVersionArtefacto() {
        this.versionArtefacto = UtilUUID.generarNuevoUUID();
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setUriArchivo(String uriArchivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(uriArchivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                EntregablesFields.ArtefactoEntregable.URI_ARCHIVO,
                EntregablesCodes.ArtefactoEntregable.URI_ARCHIVO_INVALIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, EntregablesLimits.ArtefactoEntregable.URI_ARCHIVO_MAX,
                EntregablesFields.ArtefactoEntregable.URI_ARCHIVO,
                EntregablesCodes.ArtefactoEntregable.URI_ARCHIVO_INVALIDA, result)) {
            return;
        }
        this.uriArchivo = recortado;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                EntregablesFields.ArtefactoEntregable.ARTEFACTO,
                EntregablesCodes.ArtefactoEntregable.ARTEFACTO_INVALIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setNumeroVersion(int numeroVersion, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(numeroVersion, EntregablesLimits.ArtefactoEntregable.NUMERO_VERSION_MIN,
                EntregablesFields.ArtefactoEntregable.NUMERO_VERSION,
                EntregablesCodes.ArtefactoEntregable.NUMERO_VERSION_INVALIDO, result)) {
            return;
        }
        this.numeroVersion = numeroVersion;
    }

    private void setVersionRepositorioArtefacto(UUID versionRepositorioArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionRepositorioArtefacto,
                EntregablesFields.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO,
                EntregablesCodes.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO_INVALIDA, result)) {
            return;
        }
        this.versionRepositorioArtefacto = versionRepositorioArtefacto;
    }

    private void setNombreDocumento(String nombreDocumento, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreDocumento);
        if (!ValidatorTexto.noEnBlanco(recortado,
                EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_DOCUMENTO_INVALIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, EntregablesLimits.ArtefactoEntregable.NOMBRE_DOCUMENTO_MAX,
                EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_DOCUMENTO_INVALIDO, result)) {
            return;
        }
        this.nombreDocumento = recortado;
    }

    private void setRepositorio(UUID repositorio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(repositorio,
                EntregablesFields.ArtefactoEntregable.REPOSITORIO,
                EntregablesCodes.ArtefactoEntregable.REPOSITORIO_INVALIDO, result)) {
            return;
        }
        this.repositorio = repositorio;
    }

    private void setNombreRepositorio(String nombreRepositorio, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreRepositorio);
        if (!ValidatorTexto.noEnBlanco(recortado,
                EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_REPOSITORIO_INVALIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, EntregablesLimits.ArtefactoEntregable.NOMBRE_REPOSITORIO_MAX,
                EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO,
                EntregablesCodes.ArtefactoEntregable.NOMBRE_REPOSITORIO_INVALIDO, result)) {
            return;
        }
        this.nombreRepositorio = recortado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getVersionArtefacto() {
        return versionArtefacto;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public String getUriArchivo() {
        return uriArchivo;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public UUID getVersionRepositorioArtefacto() {
        return versionRepositorioArtefacto;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public UUID getRepositorio() {
        return repositorio;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }
}
