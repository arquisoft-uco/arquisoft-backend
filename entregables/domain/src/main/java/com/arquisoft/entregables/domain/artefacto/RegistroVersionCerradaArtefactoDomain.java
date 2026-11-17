package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

public final class RegistroVersionCerradaArtefactoDomain {

    private ArtefactoDomain artefacto;
    private String claveOrigen;

    private RegistroVersionCerradaArtefactoDomain() {}

    public static RegistroVersionCerradaArtefactoDomain crear(ArtefactoDomain artefacto, String claveOrigen) {
        var registro = new RegistroVersionCerradaArtefactoDomain();
        var result = new ValidationResult();
        registro.setArtefacto(artefacto, result);
        registro.setClaveOrigen(claveOrigen, result);
        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setArtefacto(ArtefactoDomain artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                EntregablesFields.Artefacto.ID,
                EntregablesCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setClaveOrigen(String claveOrigen, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(claveOrigen);
        if (!ValidatorTexto.noEnBlanco(recortada,
                EntregablesFields.Artefacto.CLAVE_ORIGEN,
                EntregablesCodes.Artefacto.CLAVE_ORIGEN_REQUERIDA, result)) {
            return;
        }
        this.claveOrigen = recortada;
    }

    public ArtefactoDomain getArtefacto() {
        return artefacto;
    }

    public String getClaveOrigen() {
        return claveOrigen;
    }
}
