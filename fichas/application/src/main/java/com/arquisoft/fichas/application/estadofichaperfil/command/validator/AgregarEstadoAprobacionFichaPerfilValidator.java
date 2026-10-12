package com.arquisoft.fichas.application.estadofichaperfil.command.validator;

import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.DecisionFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

import java.util.List;

public interface AgregarEstadoAprobacionFichaPerfilValidator {

    void validar(DecisionFichaPerfilDomain decision, FichaPerfilDomain ficha, AsesorFichaDomain asesor,
                 EstadoFichaPerfilDomain estadoActual, ResumenEvaluacionesFicha resumen,
                 List<IntegranteFicha> integrantes);
}
