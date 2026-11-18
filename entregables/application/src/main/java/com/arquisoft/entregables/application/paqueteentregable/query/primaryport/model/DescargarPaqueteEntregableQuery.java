package com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public record DescargarPaqueteEntregableQuery(
        UUID entregableProyectoGrado,
        UUID actor,
        List<RolConsultaPaqueteEntregable> roles
) {

    public static DescargarPaqueteEntregableQuery crear(
            UUID entregableProyectoGrado, UUID actor, List<RolConsultaPaqueteEntregable> roles) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                EntregablesCodes.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(actor,
                EntregablesFields.PaqueteEntregable.ACTOR,
                EntregablesCodes.PaqueteEntregable.ACTOR_REQUERIDO, result);
        ValidatorColeccion.noVacia(roles,
                EntregablesFields.PaqueteEntregable.ROLES,
                EntregablesCodes.PaqueteEntregable.ROLES_REQUERIDOS, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new DescargarPaqueteEntregableQuery(entregableProyectoGrado, actor, List.copyOf(roles));
    }
}
