package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class SubidaVersionRepositorioArtefactoDomain {

    private UUID carga;
    private UUID repositorio;
    private UUID solicitante;
    private String nombreDocumento;
    private boolean nuevoDocumento;

    private SubidaVersionRepositorioArtefactoDomain() {}

    public static SubidaVersionRepositorioArtefactoDomain crear(
            UUID carga, UUID repositorio, UUID solicitante, String nombreDocumento, boolean nuevoDocumento) {
        var subida = new SubidaVersionRepositorioArtefactoDomain();
        var result = new ValidationResult();

        subida.setCarga(carga, result);
        subida.setRepositorio(repositorio, result);
        subida.setSolicitante(solicitante, result);
        subida.setNombreDocumento(nombreDocumento, result);
        subida.setNuevoDocumento(nuevoDocumento);

        result.lanzarSiTieneErrores();
        return subida;
    }

    private void setCarga(UUID carga, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(carga,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.CARGA,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_REQUERIDA, result)) {
            return;
        }
        this.carga = carga;
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

    private void setNombreDocumento(String nombreDocumento, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreDocumento);
        if (!ValidatorTexto.noEnBlanco(recortado,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                RepositorioArtefactosLimits.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_MAX,
                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO,
                RepositorioArtefactosCodes.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombreDocumento = recortado;
    }

    private void setNuevoDocumento(boolean nuevoDocumento) {
        this.nuevoDocumento = nuevoDocumento;
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

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public boolean isNuevoDocumento() {
        return nuevoDocumento;
    }
}
