package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.ModificarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.ExistenciaItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl.ItemCualitativoJuradoExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModificarItemCualitativoJuradoValidatorImpl
        implements ModificarItemCualitativoJuradoValidator {

    private final ItemCualitativoJuradoExistenteRule itemCualitativoJuradoExistenteRule;

    public ModificarItemCualitativoJuradoValidatorImpl() {
        this.itemCualitativoJuradoExistenteRule = new ItemCualitativoJuradoExistenteRuleImpl();
    }

    @Override
    public void validar(UUID itemCualitativoJurado, boolean existe) {
        itemCualitativoJuradoExistenteRule.validar(
                new ExistenciaItemCualitativoJurado(itemCualitativoJurado, existe));
    }
}
