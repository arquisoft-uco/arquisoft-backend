package com.arquisoft.fichas.domain.estudiantefichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class RemocionEstudianteFichaPerfilDomain {

    private UUID fichaPerfil;
    private UUID estudiante;

    private RemocionEstudianteFichaPerfilDomain() {}

    public static RemocionEstudianteFichaPerfilDomain crear(UUID fichaPerfil, UUID estudiante) {
        var transaccion = new RemocionEstudianteFichaPerfilDomain();
        var result = new ValidationResult();

        transaccion.setFichaPerfil(fichaPerfil, result);
        transaccion.setEstudiante(estudiante, result);

        result.lanzarSiTieneErrores();
        return transaccion;
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfil,
                FichasFields.EstudianteFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setEstudiante(UUID estudiante, ValidationResult result) {
        if (!DomainValidator.noNulo(estudiante,
                FichasFields.EstudianteFichaPerfil.ESTUDIANTE,
                FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO, result)) {
            return;
        }
        this.estudiante = estudiante;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public UUID getEstudiante() {
        return estudiante;
    }
}
