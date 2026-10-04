package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionEstadoFichaPerfilDomain {

    private EstadoFichaPerfilDomain estado;
    private UUID asesorFicha;

    private AgregacionEstadoFichaPerfilDomain() {}

    public static AgregacionEstadoFichaPerfilDomain crear(EstadoFichaPerfilDomain estado, UUID asesorFicha) {
        var agregacion = new AgregacionEstadoFichaPerfilDomain();
        var result = new ValidationResult();

        agregacion.setEstado(estado, result);
        agregacion.setAsesorFicha(asesorFicha, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setEstado(EstadoFichaPerfilDomain estado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estado,
                FichasFields.EstadoFichaPerfil.ESTADO_FICHA,
                FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_REQUERIDO, result)) {
            return;
        }
        this.estado = estado;
    }

    private void setAsesorFicha(UUID asesorFicha, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorFicha,
                FichasFields.EstadoFichaPerfil.ASESOR_FICHA,
                FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO, result)) {
            return;
        }
        this.asesorFicha = asesorFicha;
    }

    public EstadoFichaPerfilDomain getEstado() {
        return estado;
    }

    public UUID getAsesorFicha() {
        return asesorFicha;
    }

    public UUID getFichaPerfil() {
        return estado.getFichaPerfil();
    }
}
