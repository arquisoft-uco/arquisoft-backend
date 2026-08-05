package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;

import java.util.UUID;

public interface ModificarFichaPerfilValidator {

    void validarPropiedad(UUID fichaPerfil, UUID estudiante);

    void validarTitulo(FichaPerfilDomain ficha, String nuevoTitulo);
}
