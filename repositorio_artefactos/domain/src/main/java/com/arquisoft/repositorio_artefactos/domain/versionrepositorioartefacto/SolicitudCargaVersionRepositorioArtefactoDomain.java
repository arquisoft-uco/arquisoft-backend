package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.TipoArchivoPlantilla;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class SolicitudCargaVersionRepositorioArtefactoDomain {

    private static final String PATRON_NOMBRE_ARCHIVO_INVALIDO = "(?s).*[/\\\\\\p{Cntrl}].*";

    private UUID carga;
    private UUID repositorio;
    private UUID solicitante;
    private String nombreArchivo;
    private String tipoContenido;
    private long tamanoBytes;

    private SolicitudCargaVersionRepositorioArtefactoDomain() {}

    public static SolicitudCargaVersionRepositorioArtefactoDomain crear(
            UUID repositorio, UUID solicitante, String nombreArchivo, String tipoContenido, Long tamanoBytes) {
        var solicitud = new SolicitudCargaVersionRepositorioArtefactoDomain();
        var result = new ValidationResult();

        solicitud.setCarga();
        solicitud.setRepositorio(repositorio, result);
        solicitud.setSolicitante(solicitante, result);
        solicitud.setNombreArchivo(nombreArchivo, result);
        solicitud.setTipoContenido(tipoContenido, result);
        solicitud.setTamanoBytes(tamanoBytes, result);

        result.lanzarSiTieneErrores();
        return solicitud;
    }

    private void setCarga() {
        this.carga = UtilUUID.generarNuevoUUID();
    }

    private void setRepositorio(UUID repositorio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(repositorio,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO, result)) {
            return;
        }
        this.repositorio = repositorio;
    }

    private void setSolicitante(UUID solicitante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitante,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.SOLICITANTE,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.SOLICITANTE_REQUERIDO, result)) {
            return;
        }
        this.solicitante = solicitante;
    }

    private void setNombreArchivo(String nombreArchivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreArchivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                RepositorioArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_MAX,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result)) {
            return;
        }
        if (UtilTexto.coincidePatron(recortado, PATRON_NOMBRE_ARCHIVO_INVALIDO)) {
            result.agregarError(
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_ARCHIVO_INVALIDO,
                    Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_NOMBRE_ARCHIVO_INVALIDO, recortado));
            return;
        }
        this.nombreArchivo = recortado;
    }

    private void setTipoContenido(String tipoContenido, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(tipoContenido);
        if (!ValidatorTexto.noEnBlanco(recortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                RepositorioArtefactosLimits.VersionRepositorioArtefacto.TIPO_CONTENIDO_MAX,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tipoContenido = recortado;
        validarCorrespondenciaConExtension(result);
    }

    private void validarCorrespondenciaConExtension(ValidationResult result) {
        if (UtilTexto.esVacioONulo(nombreArchivo)) {
            return;
        }
        var plantilla = TipoArchivoPlantilla.desdeNombreArchivo(nombreArchivo);
        if (plantilla.esVacio()) {
            result.agregarError(
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_ARCHIVO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_NO_PERMITIDO,
                    Mensajes.formatear(VersionRepositorioArtefactoKey.ERROR_TIPO_NO_PERMITIDO, nombreArchivo));
            return;
        }
        if (!plantilla.getTipoContenido().equals(tipoContenido)) {
            result.agregarError(
                    RepositorioArtefactosFields.VersionRepositorioArtefacto.TIPO_CONTENIDO,
                    RepositorioArtefactosCodes.VersionRepositorioArtefacto.TIPO_CONTENIDO_NO_COINCIDE_EXTENSION,
                    Mensajes.formatear(
                            VersionRepositorioArtefactoKey.ERROR_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION,
                            nombreArchivo));
        }
    }

    private void setTamanoBytes(Long tamanoBytes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tamanoBytes,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(tamanoBytes, 1,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.TAMANO_BYTES,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        this.tamanoBytes = UtilNumero.obtenerPorDefecto(tamanoBytes);
    }

    public UUID getCarga() {
        return carga;
    }

    public UUID getRepositorio() {
        return repositorio;
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
}
