package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;

public interface RegistrarItemCuantitativoJuradoValidator {

    void validar(
            ItemCuantitativoJuradoDomain item,
            boolean categoriaExiste,
            boolean nombreYaExiste);
}
