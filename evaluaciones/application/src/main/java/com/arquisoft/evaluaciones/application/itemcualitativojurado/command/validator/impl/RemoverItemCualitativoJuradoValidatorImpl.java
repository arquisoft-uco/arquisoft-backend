package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RemoverItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.ExistenciaItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl.ItemCualitativoJuradoExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCualitativoJuradoValidatorImpl
        implements RemoverItemCualitativoJuradoValidator {

    private final ItemCualitativoJuradoExistenteRule itemCualitativoJuradoExistenteRule;

    public RemoverItemCualitativoJuradoValidatorImpl() {
        this.itemCualitativoJuradoExistenteRule = new ItemCualitativoJuradoExistenteRuleImpl();
    }

    @Override
    public void validar(UUID itemCualitativoJurado, boolean existe) {
        itemCualitativoJuradoExistenteRule.validar(
                new ExistenciaItemCualitativoJurado(itemCualitativoJurado, existe));
    }
}
