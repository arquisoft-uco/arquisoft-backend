package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RegistrarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.DisponibilidadNombreItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaCategoriaItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.CategoriaItemCuantitativoAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.NombreItemCuantitativoAsesorPorCategoriaUnicoRule;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl.CategoriaItemCuantitativoAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl.NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarItemCuantitativoAsesorValidatorImpl
        implements RegistrarItemCuantitativoAsesorValidator {

    private final CategoriaItemCuantitativoAsesorExistenteRule categoriaItemCuantitativoAsesorExistenteRule;
    private final NombreItemCuantitativoAsesorPorCategoriaUnicoRule
            nombreItemCuantitativoAsesorPorCategoriaUnicoRule;

    public RegistrarItemCuantitativoAsesorValidatorImpl() {
        this.categoriaItemCuantitativoAsesorExistenteRule =
                new CategoriaItemCuantitativoAsesorExistenteRuleImpl();
        this.nombreItemCuantitativoAsesorPorCategoriaUnicoRule =
                new NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImpl();
    }

    @Override
    public void validar(ItemCuantitativoAsesorDomain item, boolean categoriaExiste, boolean nombreYaExiste) {
        categoriaItemCuantitativoAsesorExistenteRule.validar(
                new ExistenciaCategoriaItemCuantitativoAsesor(item.getCategoria(), categoriaExiste));
        nombreItemCuantitativoAsesorPorCategoriaUnicoRule.validar(
                new DisponibilidadNombreItemCuantitativoAsesor(
                        item.getNombre(), item.getCategoria(), nombreYaExiste));
    }
}
