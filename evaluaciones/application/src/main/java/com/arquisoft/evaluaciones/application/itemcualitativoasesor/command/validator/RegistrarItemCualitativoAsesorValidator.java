package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;

public interface RegistrarItemCualitativoAsesorValidator {

    void validar(ItemCualitativoAsesorDomain item, boolean nombreYaExiste);
}
