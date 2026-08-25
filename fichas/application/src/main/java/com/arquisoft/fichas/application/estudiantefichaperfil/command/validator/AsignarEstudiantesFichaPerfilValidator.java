package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesFichaPerfilValidator {

    void validar(AgregacionEstudiantesFichaPerfilDomain entrada, boolean fichaExiste,
                 List<UUID> estudiantesExistentes, List<UUID> yaVinculados, long vinculadosActuales);
}
