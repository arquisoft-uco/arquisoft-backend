package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;

import java.util.UUID;

public interface ModificarFichaPerfilValidator {

    void validarPropiedad(UUID fichaPerfil, UUID estudiante);

    void validarTitulo(FichaPerfilAggregate ficha, String nuevoTitulo);
}
