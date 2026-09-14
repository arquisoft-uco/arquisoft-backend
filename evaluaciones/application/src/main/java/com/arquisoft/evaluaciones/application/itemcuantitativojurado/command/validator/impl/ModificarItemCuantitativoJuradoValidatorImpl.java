package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.ModificarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.ItemCuantitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.ItemCuantitativoJuradoExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModificarItemCuantitativoJuradoValidatorImpl
        implements ModificarItemCuantitativoJuradoValidator {

    private final ItemCuantitativoJuradoExistenteRule itemCuantitativoJuradoExistenteRule;

    public ModificarItemCuantitativoJuradoValidatorImpl() {
        this.itemCuantitativoJuradoExistenteRule = new ItemCuantitativoJuradoExistenteRuleImpl();
    }

    @Override
    public void validar(UUID itemCuantitativoJurado, boolean existe) {
        itemCuantitativoJuradoExistenteRule.validar(
                new ExistenciaItemCuantitativoJurado(itemCuantitativoJurado, existe));
    }
}
