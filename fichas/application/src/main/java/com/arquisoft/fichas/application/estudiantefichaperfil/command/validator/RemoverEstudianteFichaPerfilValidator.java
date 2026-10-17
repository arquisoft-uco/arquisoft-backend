package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;

import java.util.List;
import java.util.UUID;

public interface RemoverEstudianteFichaPerfilValidator {

    void validar(RemocionEstudianteFichaPerfilDomain entrada, boolean fichaExiste,
                 EstadoFichaPerfilDomain estadoActual, List<UUID> estudiantesExistentes,
                 boolean vinculoExiste);
}
