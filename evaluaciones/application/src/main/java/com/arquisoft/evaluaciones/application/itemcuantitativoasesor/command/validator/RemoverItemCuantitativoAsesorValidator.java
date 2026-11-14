package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import java.util.UUID;

public interface RemoverItemCuantitativoAsesorValidator {

    void validar(UUID itemCuantitativoAsesor, boolean existe, boolean enUso);
}
