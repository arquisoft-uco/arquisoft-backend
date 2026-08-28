package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;

public interface RegistrarItemCualitativoJuradoValidator {

    void validar(ItemCualitativoJuradoDomain item, boolean nombreYaExiste);
}
