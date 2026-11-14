package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.ModificarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.ItemCuantitativoAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl.ItemCuantitativoAsesorExistenteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModificarItemCuantitativoAsesorValidatorImpl
        implements ModificarItemCuantitativoAsesorValidator {

    private final ItemCuantitativoAsesorExistenteRule itemCuantitativoAsesorExistenteRule;

    public ModificarItemCuantitativoAsesorValidatorImpl() {
        this.itemCuantitativoAsesorExistenteRule = new ItemCuantitativoAsesorExistenteRuleImpl();
    }

    @Override
    public void validar(UUID itemCuantitativoAsesor, boolean existe) {
        itemCuantitativoAsesorExistenteRule.validar(
                new ExistenciaItemCuantitativoAsesor(itemCuantitativoAsesor, existe));
    }
}
