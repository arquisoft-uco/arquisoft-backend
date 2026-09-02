package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public interface AsignarEstudiantesFichaPerfilValidator {

    void validar(AgregacionEstudiantesFichaPerfilDomain entrada, FichaPerfilDomain ficha,
                 List<UUID> estudiantesExistentes, List<UUID> yaVinculados, long vinculadosActuales);
}
