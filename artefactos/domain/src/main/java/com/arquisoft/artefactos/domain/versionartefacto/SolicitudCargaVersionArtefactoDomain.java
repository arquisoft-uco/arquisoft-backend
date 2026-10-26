package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.artefactos.domain.versionartefacto.model.TipoArchivoVersionArtefacto;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class SolicitudCargaVersionArtefactoDomain {

    private static final String PATRON_NOMBRE_ARCHIVO_INVALIDO = "(?s).*[/\\\\\\p{Cntrl}].*";

    private UUID carga;
    private UUID artefacto;
    private UUID solicitante;
    private String nombreArchivo;
    private String tipoContenido;
    private long tamanoBytes;

    private SolicitudCargaVersionArtefactoDomain() {}

    public static SolicitudCargaVersionArtefactoDomain crear(
            UUID artefacto, UUID solicitante, String nombreArchivo, String tipoContenido, Long tamanoBytes) {
        var solicitud = new SolicitudCargaVersionArtefactoDomain();
        var result = new ValidationResult();

        solicitud.setCarga();
        solicitud.setArtefacto(artefacto, result);
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

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.ARTEFACTO_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setSolicitante(UUID solicitante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result)) {
            return;
        }
        this.solicitante = solicitante;
    }

    private void setNombreArchivo(String nombreArchivo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreArchivo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionArtefacto.NOMBRE_ARCHIVO_MAX,
                ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_DEMASIADO_LARGO, result)) {
            return;
        }
        if (UtilTexto.coincidePatron(recortado, PATRON_NOMBRE_ARCHIVO_INVALIDO)) {
            result.agregarError(
                    ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                    ArtefactosCodes.VersionArtefacto.NOMBRE_ARCHIVO_INVALIDO,
                    Mensajes.formatear(VersionArtefactoKey.ERROR_NOMBRE_ARCHIVO_INVALIDO, recortado));
            return;
        }
        this.nombreArchivo = recortado;
    }

    private void setTipoContenido(String tipoContenido, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(tipoContenido);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.VersionArtefacto.TIPO_CONTENIDO_MAX,
                ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tipoContenido = recortado;
        validarCorrespondenciaConExtension(result);
    }

    private void validarCorrespondenciaConExtension(ValidationResult result) {
        if (UtilTexto.esVacioONulo(nombreArchivo)) {
            return;
        }
        var tipo = TipoArchivoVersionArtefacto.desdeNombreArchivo(nombreArchivo);
        if (tipo.esVacio()) {
            result.agregarError(
                    ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO,
                    ArtefactosCodes.VersionArtefacto.TIPO_NO_PERMITIDO,
                    Mensajes.formatear(VersionArtefactoKey.ERROR_TIPO_NO_PERMITIDO, nombreArchivo));
            return;
        }
        if (!tipo.getTipoContenido().equals(tipoContenido)) {
            result.agregarError(
                    ArtefactosFields.VersionArtefacto.TIPO_CONTENIDO,
                    ArtefactosCodes.VersionArtefacto.TIPO_CONTENIDO_NO_COINCIDE_EXTENSION,
                    Mensajes.formatear(
                            VersionArtefactoKey.ERROR_TIPO_CONTENIDO_NO_COINCIDE_EXTENSION,
                            tipoContenido, nombreArchivo));
        }
    }

    private void setTamanoBytes(Long tamanoBytes, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tamanoBytes,
                ArtefactosFields.VersionArtefacto.TAMANO_BYTES,
                ArtefactosCodes.VersionArtefacto.TAMANO_BYTES_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(tamanoBytes, 1,
                ArtefactosFields.VersionArtefacto.TAMANO_BYTES,
                ArtefactosCodes.VersionArtefacto.TAMANO_BYTES_INVALIDO, result)) {
            return;
        }
        this.tamanoBytes = UtilNumero.obtenerPorDefecto(tamanoBytes);
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
}
