package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class PurgaPaquetesAnterioresDomain {

    private static final int VERSION_MINIMA = 1;

    private UUID proyectoGrado;
    private int versionEntregable;

    private PurgaPaquetesAnterioresDomain() {}

    public static PurgaPaquetesAnterioresDomain crear(UUID proyectoGrado, Integer versionEntregable) {
        var purga = new PurgaPaquetesAnterioresDomain();
        var result = new ValidationResult();
        purga.setProyectoGrado(proyectoGrado, result);
        purga.setVersionEntregable(versionEntregable, result);
        result.lanzarSiTieneErrores();
        return purga;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersionEntregable(Integer versionEntregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionEntregable,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        if (!ValidatorNumero.valorMinimo(versionEntregable, VERSION_MINIMA,
                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE,
                ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA, result)) {
            return;
        }
        this.versionEntregable = versionEntregable;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }
}
