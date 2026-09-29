package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RegistrarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.DisponibilidadNombreItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaCategoriaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.CategoriaItemCuantitativoJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.NombreItemCuantitativoJuradoPorCategoriaUnicoRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.CategoriaItemCuantitativoJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl.NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarItemCuantitativoJuradoValidatorImpl
        implements RegistrarItemCuantitativoJuradoValidator {

    private final CategoriaItemCuantitativoJuradoExistenteRule categoriaExistenteRule;
    private final NombreItemCuantitativoJuradoPorCategoriaUnicoRule nombreUnicoRule;

    public RegistrarItemCuantitativoJuradoValidatorImpl() {
        this.categoriaExistenteRule = new CategoriaItemCuantitativoJuradoExistenteRuleImpl();
        this.nombreUnicoRule = new NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl();
    }

    @Override
    public void validar(
            ItemCuantitativoJuradoDomain item,
            boolean categoriaExiste,
            boolean nombreYaExiste) {
        categoriaExistenteRule.validar(
                new ExistenciaCategoriaItemCuantitativoJurado(
                        item.getCategoria(), categoriaExiste));
        nombreUnicoRule.validar(
                new DisponibilidadNombreItemCuantitativoJurado(
                        item.getNombre(), item.getCategoria(), nombreYaExiste));
    }
}
