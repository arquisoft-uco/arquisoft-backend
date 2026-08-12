package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

public interface RegistrarFichaPerfilValidator {

    void validar(FichaPerfilDomain ficha, boolean asesorExiste, boolean tituloYaExiste);
}
