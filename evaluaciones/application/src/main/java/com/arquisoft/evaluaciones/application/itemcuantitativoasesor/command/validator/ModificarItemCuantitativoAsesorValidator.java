package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import java.util.UUID;

public interface ModificarItemCuantitativoAsesorValidator {

    void validar(UUID itemCuantitativoAsesor, boolean existe);
}
