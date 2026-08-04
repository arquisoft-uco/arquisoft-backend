package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesFichaPerfilValidator {

    void validar(UUID fichaPerfil, List<UUID> estudiantes, List<EstudianteFichaPerfilAggregate> relaciones);
}
