package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RemoverItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.UsoItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.ItemCuantitativoAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.ItemCuantitativoAsesorSinUsoRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl.ItemCuantitativoAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl.ItemCuantitativoAsesorSinUsoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCuantitativoAsesorValidatorImpl
        implements RemoverItemCuantitativoAsesorValidator {

    private final ItemCuantitativoAsesorExistenteRule itemCuantitativoAsesorExistenteRule;
    private final ItemCuantitativoAsesorSinUsoRule itemCuantitativoAsesorSinUsoRule;

    public RemoverItemCuantitativoAsesorValidatorImpl() {
        this.itemCuantitativoAsesorExistenteRule = new ItemCuantitativoAsesorExistenteRuleImpl();
        this.itemCuantitativoAsesorSinUsoRule = new ItemCuantitativoAsesorSinUsoRuleImpl();
    }

    @Override
    public void validar(UUID itemCuantitativoAsesor, boolean existe, boolean enUso) {
        itemCuantitativoAsesorExistenteRule.validar(
                new ExistenciaItemCuantitativoAsesor(itemCuantitativoAsesor, existe));
        itemCuantitativoAsesorSinUsoRule.validar(
                new UsoItemCuantitativoAsesor(itemCuantitativoAsesor, enUso));
    }
}
