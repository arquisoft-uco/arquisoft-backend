package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesFichaPerfilValidator {

    void validar(UUID fichaPerfil, List<UUID> estudiantes, List<EstudianteFichaPerfilDomain> relaciones);
}
