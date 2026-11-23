package com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarEntregableProyectoGradoCommand(
        UUID entregableProyectoGrado,
        UUID proyectoGrado,
        Integer version,
        String estadoEntregable,
        String claveObjeto,
        String nombreArchivo
) {
    private static final int VERSION_MINIMA = 1;

    public static AgregarEntregableProyectoGradoCommand crear(
            String entregableProyectoGrado, String proyectoGrado, Integer version, String estadoEntregable,
            String claveObjeto, String nombreArchivo) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(entregableProyectoGrado,
                BibliotecaFields.EntregableProyectoGrado.ID,
                BibliotecaCodes.EntregableProyectoGrado.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                BibliotecaFields.EntregableProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        if (ValidatorObjeto.noNulo(version,
                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                BibliotecaCodes.EntregableProyectoGrado.VERSION_INVALIDA, result)) {
            ValidatorNumero.valorMinimo(version, VERSION_MINIMA,
                    BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                    BibliotecaCodes.EntregableProyectoGrado.VERSION_INVALIDA, result);
        }
        ValidatorTexto.noEnBlanco(estadoEntregable,
                BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                BibliotecaCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(claveObjeto,
                BibliotecaFields.EntregableProyectoGrado.CLAVE_OBJETO,
                BibliotecaCodes.EntregableProyectoGrado.CLAVE_OBJETO_REQUERIDA, result);
        ValidatorTexto.noEnBlanco(nombreArchivo,
                BibliotecaFields.EntregableProyectoGrado.NOMBRE_ARCHIVO,
                BibliotecaCodes.EntregableProyectoGrado.NOMBRE_ARCHIVO_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEntregableProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado),
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                version, estadoEntregable, claveObjeto, nombreArchivo);
    }
}
