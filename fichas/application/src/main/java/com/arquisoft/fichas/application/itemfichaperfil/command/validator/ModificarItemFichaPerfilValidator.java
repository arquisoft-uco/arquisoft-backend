package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

import java.util.UUID;

public interface ModificarItemFichaPerfilValidator {

    void validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste,
                 boolean esPropietario, EstadoFichaPerfilDomain estadoActual);
}
