package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesFichaPerfilValidator {

    void validar(List<EstudianteFichaPerfilDomain> relaciones, boolean fichaExiste,
                 List<UUID> estudiantesExistentes, List<UUID> yaVinculados, long vinculadosActuales);
}
