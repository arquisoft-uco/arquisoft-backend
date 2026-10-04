package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.AgregacionEstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

import java.util.List;

public interface AgregarEstadoFichaPerfilValidator {

    void validar(AgregacionEstadoFichaPerfilDomain entrada, FichaPerfilDomain ficha,
                 EstadoFichaPerfilDomain estadoActual, ResumenEvaluacionesFicha resumen,
                 List<IntegranteFicha> integrantes);
}
