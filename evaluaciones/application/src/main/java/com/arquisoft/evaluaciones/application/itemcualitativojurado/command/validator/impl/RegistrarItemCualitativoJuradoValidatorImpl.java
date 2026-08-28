package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RegistrarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.DisponibilidadNombreItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.NombreItemCualitativoJuradoUnicoRule;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl.NombreItemCualitativoJuradoUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarItemCualitativoJuradoValidatorImpl
        implements RegistrarItemCualitativoJuradoValidator {

    private final NombreItemCualitativoJuradoUnicoRule nombreItemCualitativoJuradoUnicoRule;

    public RegistrarItemCualitativoJuradoValidatorImpl() {
        this.nombreItemCualitativoJuradoUnicoRule =
                new NombreItemCualitativoJuradoUnicoRuleImpl();
    }

    @Override
    public void validar(ItemCualitativoJuradoDomain item, boolean nombreYaExiste) {
        nombreItemCualitativoJuradoUnicoRule.validar(
                new DisponibilidadNombreItemCualitativoJurado(
                        item.getNombre(), nombreYaExiste));
    }
}
