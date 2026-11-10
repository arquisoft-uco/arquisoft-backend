package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RemoverItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.ExistenciaItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.UsoItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.ItemCualitativoAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.ItemCualitativoAsesorSinUsoRule;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl.ItemCualitativoAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl.ItemCualitativoAsesorSinUsoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCualitativoAsesorValidatorImpl
        implements RemoverItemCualitativoAsesorValidator {

    private final ItemCualitativoAsesorExistenteRule itemCualitativoAsesorExistenteRule;
    private final ItemCualitativoAsesorSinUsoRule itemCualitativoAsesorSinUsoRule;

    public RemoverItemCualitativoAsesorValidatorImpl() {
        this.itemCualitativoAsesorExistenteRule = new ItemCualitativoAsesorExistenteRuleImpl();
        this.itemCualitativoAsesorSinUsoRule = new ItemCualitativoAsesorSinUsoRuleImpl();
    }

    @Override
    public void validar(UUID itemCualitativoAsesor, boolean existe, boolean enUso) {
        itemCualitativoAsesorExistenteRule.validar(
                new ExistenciaItemCualitativoAsesor(itemCualitativoAsesor, existe));
        itemCualitativoAsesorSinUsoRule.validar(
                new UsoItemCualitativoAsesor(itemCualitativoAsesor, enUso));
    }
}
