package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RemoverItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.ExistenciaItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.UsoItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoSinUsoRule;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl.ItemCualitativoJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl.ItemCualitativoJuradoSinUsoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCualitativoJuradoValidatorImpl
        implements RemoverItemCualitativoJuradoValidator {

    private final ItemCualitativoJuradoExistenteRule itemCualitativoJuradoExistenteRule;
    private final ItemCualitativoJuradoSinUsoRule itemCualitativoJuradoSinUsoRule;

    public RemoverItemCualitativoJuradoValidatorImpl() {
        this.itemCualitativoJuradoExistenteRule = new ItemCualitativoJuradoExistenteRuleImpl();
        this.itemCualitativoJuradoSinUsoRule = new ItemCualitativoJuradoSinUsoRuleImpl();
    }

    @Override
    public void validar(UUID itemCualitativoJurado, boolean existe, boolean enUso) {
        itemCualitativoJuradoExistenteRule.validar(
                new ExistenciaItemCualitativoJurado(itemCualitativoJurado, existe));
        itemCualitativoJuradoSinUsoRule.validar(
                new UsoItemCualitativoJurado(itemCualitativoJurado, enUso));
    }
}
