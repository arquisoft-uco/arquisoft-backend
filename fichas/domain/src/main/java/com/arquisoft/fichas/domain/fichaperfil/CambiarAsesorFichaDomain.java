package com.arquisoft.fichas.domain.fichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class CambiarAsesorFichaDomain {

    private UUID fichaPerfil;
    private UUID nuevoAsesorFicha;

    private CambiarAsesorFichaDomain() {}

    public static CambiarAsesorFichaDomain crear(UUID fichaPerfil, UUID nuevoAsesorFicha) {
        var transaccion = new CambiarAsesorFichaDomain();
        var result = new ValidationResult();

        transaccion.setFichaPerfil(fichaPerfil, result);
        transaccion.setNuevoAsesorFicha(nuevoAsesorFicha, result);

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

    private void setNuevoAsesorFicha(UUID nuevoAsesorFicha, ValidationResult result) {
        if (!DomainValidator.noNulo(nuevoAsesorFicha,
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.nuevoAsesorFicha = nuevoAsesorFicha;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public UUID getNuevoAsesorFicha() {
        return nuevoAsesorFicha;
    }
}
