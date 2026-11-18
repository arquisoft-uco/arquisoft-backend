package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record ActualizarEstadoEntregableProyectoGradoCommand(
        UUID entregableProyectoGrado,
        UUID proyectoGrado,
        int version,
        String estadoEntregable,
        Instant ocurridoEn
) {

    public static ActualizarEstadoEntregableProyectoGradoCommand crear(
            String entregableProyectoGrado, String proyectoGrado, int version, String estadoEntregable,
            Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(entregableProyectoGrado,
                ProyectosFields.EntregableProyectoGrado.ID,
                ProyectosCodes.EntregableProyectoGrado.ID_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                ProyectosFields.EntregableProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorNumero.valorMinimo(version, 1,
                ProyectosFields.EntregableProyectoGrado.VERSION,
                ProyectosCodes.EntregableProyectoGrado.VERSION_INVALIDA, result);
        ValidatorTexto.noEnBlanco(estadoEntregable,
                ProyectosFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ProyectosFields.EntregableProyectoGrado.OCURRIDO_EN,
                ProyectosCodes.EntregableProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarEstadoEntregableProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(entregableProyectoGrado),
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                version, estadoEntregable, ocurridoEn);
    }
}
