package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RemoverItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.ExistenciaItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.ItemCualitativoAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl.ItemCualitativoAsesorExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCualitativoAsesorValidatorImpl
        implements RemoverItemCualitativoAsesorValidator {

    private final ItemCualitativoAsesorExistenteRule itemCualitativoAsesorExistenteRule;

    public RemoverItemCualitativoAsesorValidatorImpl() {
        this.itemCualitativoAsesorExistenteRule = new ItemCualitativoAsesorExistenteRuleImpl();
    }

    @Override
    public void validar(UUID itemCualitativoAsesor, boolean existe) {
        itemCualitativoAsesorExistenteRule.validar(
                new ExistenciaItemCualitativoAsesor(itemCualitativoAsesor, existe));
    }
}
