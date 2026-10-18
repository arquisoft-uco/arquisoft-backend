package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import java.util.UUID;

public interface RemoverItemCuantitativoJuradoValidator {

    void validar(UUID itemCuantitativoJurado, boolean existe, boolean enUso);
}
