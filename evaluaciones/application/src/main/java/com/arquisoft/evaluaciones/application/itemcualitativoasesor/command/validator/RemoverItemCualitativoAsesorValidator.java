package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import java.util.UUID;

public interface RemoverItemCualitativoAsesorValidator {

    void validar(UUID itemCualitativoAsesor, boolean existe, boolean enUso);
}
