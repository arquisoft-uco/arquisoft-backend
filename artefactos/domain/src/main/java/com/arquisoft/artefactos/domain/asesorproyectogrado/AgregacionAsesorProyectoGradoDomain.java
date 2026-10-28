package com.arquisoft.artefactos.domain.asesorproyectogrado;

import com.arquisoft.artefactos.domain.artefacto.TraspasoArtefactosDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;

public final class AgregacionAsesorProyectoGradoDomain {

    private AsesorProyectoGradoDomain vinculo;
    private TraspasoArtefactosDomain traspaso;

    private AgregacionAsesorProyectoGradoDomain() {}

    public static AgregacionAsesorProyectoGradoDomain crear(
            AsesorProyectoGradoDomain vinculo, TraspasoArtefactosDomain traspaso) {
        var agregacion = new AgregacionAsesorProyectoGradoDomain();
        var result = new ValidationResult();
        agregacion.setVinculo(vinculo, result);
        agregacion.setTraspaso(traspaso, result);
        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setVinculo(AsesorProyectoGradoDomain vinculo, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(vinculo,
                ArtefactosFields.AsesorProyectoGrado.VINCULO,
                ArtefactosCodes.AsesorProyectoGrado.VINCULO_REQUERIDO, result)) {
            return;
        }
        this.vinculo = vinculo;
    }

    private void setTraspaso(TraspasoArtefactosDomain traspaso, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(traspaso,
                ArtefactosFields.AsesorProyectoGrado.TRASPASO,
                ArtefactosCodes.AsesorProyectoGrado.TRASPASO_REQUERIDO, result)) {
            return;
        }
        this.traspaso = traspaso;
    }

    public boolean esVinculoMasReciente(Instant ultimoOcurridoEn) {
        return vinculo.getOcurridoEn().isAfter(ultimoOcurridoEn);
    }

    public AsesorProyectoGradoDomain getVinculo() {
        return vinculo;
    }

    public TraspasoArtefactosDomain getTraspaso() {
        return traspaso;
    }
}
