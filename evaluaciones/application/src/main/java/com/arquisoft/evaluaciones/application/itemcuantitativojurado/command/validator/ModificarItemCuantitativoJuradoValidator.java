package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import java.util.UUID;

public interface ModificarItemCuantitativoJuradoValidator {

    void validar(UUID itemCuantitativoJurado, boolean existe);
}
