package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import java.util.UUID;

public interface RemoverItemCualitativoJuradoValidator {

    void validar(UUID itemCualitativoJurado, boolean existe, boolean enUso);
}
