package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import java.util.UUID;

public interface ModificarItemCualitativoAsesorValidator {

    void validar(UUID itemCualitativoAsesor, boolean existe);
}
