package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;

public interface ModificarFichaPerfilValidator {

    void validar(ModificacionFichaPerfilDomain modificacion, boolean esPropietario,
                 EstadoFichaPerfilDomain estadoActual, boolean tituloYaExiste);
}
