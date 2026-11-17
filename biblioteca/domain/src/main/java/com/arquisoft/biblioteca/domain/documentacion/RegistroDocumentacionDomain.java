package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroDocumentacionDomain {

    private UUID entregableProyectoGrado;
    private UUID bibliotecario;

    private RegistroDocumentacionDomain() {}

    public static RegistroDocumentacionDomain crear(UUID entregableProyectoGrado, UUID bibliotecario) {
        var registro = new RegistroDocumentacionDomain();
        var result = new ValidationResult();

        registro.setEntregableProyectoGrado(entregableProyectoGrado, result);
        registro.setBibliotecario(bibliotecario, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                BibliotecaFields.Documentacion.ENTREGABLE_PROYECTO_GRADO,
                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setBibliotecario(UUID bibliotecario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(bibliotecario,
                BibliotecaFields.Documentacion.BIBLIOTECARIO,
                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO, result)) {
            return;
        }
        this.bibliotecario = bibliotecario;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getBibliotecario() {
        return bibliotecario;
    }
}
