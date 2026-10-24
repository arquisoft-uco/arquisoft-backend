package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioNombreRepositorioDomain {

    private UUID repositorio;
    private String nombreRepositorio;

    private CambioNombreRepositorioDomain() {}

    public static CambioNombreRepositorioDomain crear(UUID repositorio, String nombreRepositorio) {
        var cambio = new CambioNombreRepositorioDomain();
        var result = new ValidationResult();

        cambio.setRepositorio(repositorio, result);
        cambio.setNombreRepositorio(nombreRepositorio, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setRepositorio(UUID repositorio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(repositorio,
                ArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_ARTEFACTO_REQUERIDO, result)) {
            return;
        }
        this.repositorio = repositorio;
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

    public UUID getRepositorio() {
        return repositorio;
    }

    public String getNombreRepositorio() {
        return nombreRepositorio;
    }
}
