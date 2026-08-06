package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class ModificarFichaPerfilDomain {

    private UUID fichaPerfil;
    private String tituloProyecto;
    private UUID estudiante;

    private ModificarFichaPerfilDomain() {}

    public static ModificarFichaPerfilDomain crear(UUID fichaPerfil, String tituloProyecto, UUID estudiante) {
        var transaccion = new ModificarFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setFichaPerfil(fichaPerfil, result);
        transaccion.setTituloProyecto(tituloProyecto, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfil,
                FichasFields.FichaPerfil.ID,
                FichasCodes.FichaPerfil.ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setTituloProyecto(String tituloProyecto, ValidationResult result) {
        if (!DomainValidator.noEnBlanco(tituloProyecto,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!DomainValidator.longitudMaxima(tituloProyecto,
                FichasLimits.FichaPerfil.TITULO_MAX,
                FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result)) {
            return;
        }
        this.tituloProyecto = UtilText.applyTrim(tituloProyecto);
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!DomainValidator.noNulo(estudiante,
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
