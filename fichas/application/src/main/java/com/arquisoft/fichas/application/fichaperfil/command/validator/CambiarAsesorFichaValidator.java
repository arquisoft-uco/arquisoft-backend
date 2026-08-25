package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

public interface CambiarAsesorFichaValidator {

    void validar(CambioAsesorFichaDomain cambio, FichaPerfilDomain ficha, AsesorFichaDomain asesorFicha,
                 EstadoFichaPerfilDomain estadoActual);
}
