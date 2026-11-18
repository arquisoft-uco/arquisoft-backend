package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RegistrarItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.DisponibilidadNombreItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.NombreItemCualitativoAsesorUnicoRule;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl.NombreItemCualitativoAsesorUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarItemCualitativoAsesorValidatorImpl implements RegistrarItemCualitativoAsesorValidator {

    private final NombreItemCualitativoAsesorUnicoRule nombreItemCualitativoAsesorUnicoRule;

    public RegistrarItemCualitativoAsesorValidatorImpl() {
        this.nombreItemCualitativoAsesorUnicoRule = new NombreItemCualitativoAsesorUnicoRuleImpl();
    }

    @Override
    public void validar(ItemCualitativoAsesorDomain item, boolean nombreYaExiste) {
        nombreItemCualitativoAsesorUnicoRule.validar(
                new DisponibilidadNombreItemCualitativoAsesor(item.getNombre(), nombreYaExiste));
    }
}
