package com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator;

import java.util.UUID;

public interface AgregarRepresentanteComiteCurriculumValidator {

    void validar(UUID usuario, boolean usuarioExiste, boolean yaEsRepresentante);
}
