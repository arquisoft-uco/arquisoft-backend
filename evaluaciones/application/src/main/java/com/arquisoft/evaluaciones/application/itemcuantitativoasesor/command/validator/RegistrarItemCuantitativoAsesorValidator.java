package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;

public interface RegistrarItemCuantitativoAsesorValidator {

    void validar(ItemCuantitativoAsesorDomain item, boolean categoriaExiste, boolean nombreYaExiste);
}
