package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.message.constant.RepositorioArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class RepositorioArtefactoDomain {

    private UUID id;
    private String nombreRepositorio;
    private String descripcion;
    private UUID administrador;

    private RepositorioArtefactoDomain() {}

    public static RepositorioArtefactoDomain crear(
            String nombreRepositorio, String descripcion, UUID administrador) {
        var repositorio = new RepositorioArtefactoDomain();
        var result = new ValidationResult();

        repositorio.setId();
        repositorio.setNombreRepositorio(nombreRepositorio, result);
        repositorio.setDescripcion(descripcion, result);
        repositorio.setAdministrador(administrador, result);

        result.lanzarSiTieneErrores();
        return repositorio;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setNombreRepositorio(String nombreRepositorio, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombreRepositorio);
        if (!ValidatorTexto.noEnBlanco(recortado,
                RepositorioArtefactosFields.RepositorioArtefacto.NOMBRE_REPOSITORIO,
                RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                RepositorioArtefactosLimits.RepositorioArtefacto.NOMBRE_MAX,
                RepositorioArtefactosFields.RepositorioArtefacto.NOMBRE_REPOSITORIO,
                RepositorioArtefactosCodes.RepositorioArtefacto.NOMBRE_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombreRepositorio = recortado;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                RepositorioArtefactosFields.RepositorioArtefacto.DESCRIPCION,
                RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada,
                RepositorioArtefactosLimits.RepositorioArtefacto.DESCRIPCION_MAX,
                RepositorioArtefactosFields.RepositorioArtefacto.DESCRIPCION,
                RepositorioArtefactosCodes.RepositorioArtefacto.DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setAdministrador(UUID administrador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(administrador,
                RepositorioArtefactosFields.RepositorioArtefacto.ADMINISTRADOR,
                RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_REQUERIDO, result)) {
            return;
        }
        this.administrador = administrador;
    }

    public UUID getId() {
        return id;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public UUID getAdministrador() {
        return administrador;
    }
}
