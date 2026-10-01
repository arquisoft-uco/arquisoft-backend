package com.arquisoft.fichas.domain.fichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionFichaPerfilDomain {

    private UUID fichaPerfil;
    private String tituloProyecto;
    private UUID estudiante;

    private ModificacionFichaPerfilDomain() {}

    public static ModificacionFichaPerfilDomain crear(UUID fichaPerfil, String tituloProyecto, UUID estudiante) {
        var transaccion = new ModificacionFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setFichaPerfil(fichaPerfil, result);
        transaccion.setTituloProyecto(tituloProyecto, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID,
                FichasCodes.FichaPerfil.ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setTituloProyecto(String tituloProyecto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(tituloProyecto,
                FichasLimits.FichaPerfil.TITULO_MAX,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilTexto.aplicarTrim(tituloProyecto);
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estudiante,
                FichasFields.FichaPerfil.ESTUDIANTE,
                FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
