package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class DecisionFichaPerfilDomain {

    private UUID fichaPerfil;
    private boolean acepta;
    private UUID coordinador;

    private DecisionFichaPerfilDomain() {}

    public static DecisionFichaPerfilDomain crear(UUID fichaPerfil, boolean acepta, UUID coordinador) {
        var decision = new DecisionFichaPerfilDomain();
        var result = new ValidationResult();

        decision.setFichaPerfil(fichaPerfil, result);
        decision.setAcepta(acepta);
        decision.setCoordinador(coordinador, result);

        result.lanzarSiTieneErrores();
        return decision;
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setAcepta(boolean acepta) {
        this.acepta = acepta;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                FichasFields.EstadoFichaPerfil.COORDINADOR,
                FichasCodes.EstadoFichaPerfil.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public boolean isAcepta() {
        return acepta;
    }

    public UUID getCoordinador() {
        return coordinador;
    }
}
