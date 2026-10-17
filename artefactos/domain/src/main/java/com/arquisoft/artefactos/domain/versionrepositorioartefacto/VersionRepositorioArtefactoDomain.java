package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class VersionRepositorioArtefactoDomain {

    public record DatosArchivo(String nombreArchivo, String tipoContenido, long tamanoBytes) {}

    private UUID id;
    private String nombreDocumento;
    private int versionDocumento;
    private UUID repositorioArtefacto;
    private String nombreRepositorio;
    private String nombreArchivo;
    private String tipoContenido;
    private long tamanoBytes;

    private VersionRepositorioArtefactoDomain() {}

    public static VersionRepositorioArtefactoDomain crear(
            UUID id, String nombreDocumento, int versionDocumento, UUID repositorioArtefacto,
            String nombreRepositorio, DatosArchivo archivo) {
        var version = new VersionRepositorioArtefactoDomain();
        var result = new ValidationResult();

        version.setId(id, result);
        version.setNombreDocumento(nombreDocumento, result);
        version.setVersionDocumento(versionDocumento, result);
        version.setRepositorioArtefacto(repositorioArtefacto, result);
        version.setNombreRepositorio(nombreRepositorio, result);
        version.setNombreArchivo(archivo.nombreArchivo(), result);
        version.setTipoContenido(archivo.tipoContenido(), result);
        version.setTamanoBytes(archivo.tamanoBytes(), result);

        result.lanzarSiTieneErrores();
        return version;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ArtefactosFields.VersionRepositorioArtefacto.ID,
                ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombreDocumento(String nombreDocumento, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreDocumento);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombreDocumento = recortado;
    }

    private void setVersionDocumento(int versionDocumento, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(versionDocumento, 1,
                ArtefactosFields.VersionRepositorioArtefacto.VERSION_DOCUMENTO,
                ArtefactosCodes.VersionRepositorioArtefacto.VERSION_DOCUMENTO_INVALIDA, result)) {
            return;
        }
        this.versionDocumento = versionDocumento;
    }

    private void setRepositorioArtefacto(UUID repositorioArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(repositorioArtefacto,
                ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO_REQUERIDO, result)) {
            return;
        }
        this.repositorioArtefacto = repositorioArtefacto;
    }

    private void setNombreRepositorio(String nombreRepositorio, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreRepositorio);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_REPOSITORIO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombreRepositorio = recortado;
    }

    private void setNombreArchivo(String nombreArchivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreArchivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombreArchivo = recortado;
    }

    private void setTipoContenido(String tipoContenido, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(tipoContenido);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionRepositorioArtefacto.TIPO_CONTENIDO_MAX,
                ArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tipoContenido = recortado;
    }

    private void setTamanoBytes(long tamanoBytes, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(tamanoBytes, 1,
                ArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                ArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        this.tamanoBytes = tamanoBytes;
    }

    public UUID getId() {
        return id;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public int getVersionDocumento() {
        return versionDocumento;
    }

    public UUID getRepositorioArtefacto() {
        return repositorioArtefacto;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
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
}
