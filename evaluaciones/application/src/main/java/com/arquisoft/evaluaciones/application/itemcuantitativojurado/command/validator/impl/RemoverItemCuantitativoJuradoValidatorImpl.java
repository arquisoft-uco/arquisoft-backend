package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RemoverItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.UsoItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.ItemCuantitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.ItemCuantitativoJuradoSinUsoRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.ItemCuantitativoJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.ItemCuantitativoJuradoSinUsoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemCuantitativoJuradoValidatorImpl
        implements RemoverItemCuantitativoJuradoValidator {

    private final ItemCuantitativoJuradoExistenteRule itemCuantitativoJuradoExistenteRule;
    private final ItemCuantitativoJuradoSinUsoRule itemCuantitativoJuradoSinUsoRule;

    public RemoverItemCuantitativoJuradoValidatorImpl() {
        this.itemCuantitativoJuradoExistenteRule = new ItemCuantitativoJuradoExistenteRuleImpl();
        this.itemCuantitativoJuradoSinUsoRule = new ItemCuantitativoJuradoSinUsoRuleImpl();
    }

    @Override
    public void validar(UUID itemCuantitativoJurado, boolean existe, boolean enUso) {
        itemCuantitativoJuradoExistenteRule.validar(
                new ExistenciaItemCuantitativoJurado(itemCuantitativoJurado, existe));
        itemCuantitativoJuradoSinUsoRule.validar(
                new UsoItemCuantitativoJurado(itemCuantitativoJurado, enUso));
    }
}
