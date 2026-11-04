package com.arquisoft.entregables.application.entregableproyectogrado.query.validator;

import java.util.UUID;

public interface ConsultarEntregablesProyectoGradoEstudianteValidator {

    void validar(UUID estudiante, UUID proyectoGrado, boolean pertenece);
}
