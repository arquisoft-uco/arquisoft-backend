package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import java.util.UUID;

public interface ModificarItemCualitativoJuradoValidator {

    void validar(UUID itemCualitativoJurado, boolean existe);
}
