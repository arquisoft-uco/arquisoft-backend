package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantePropietarioFichaRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemSinRevisionesRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverItemFichaPerfilValidatorImpl implements RemoverItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final ItemSinRevisionesRule itemSinRevisionesRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public RemoverItemFichaPerfilValidatorImpl() {
        this.itemFichaPerfilExisteRule = new ItemFichaPerfilExisteRuleImpl();
        this.estudiantePropietarioFichaRule = new EstudiantePropietarioFichaRuleImpl();
        this.itemSinRevisionesRule = new ItemSinRevisionesRuleImpl();
    }

    @Override
    public void validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste,
                        boolean esPropietario, long totalRevisiones) {

        itemFichaPerfilExisteRule.validar(new ExistenciaItemFichaPerfil(item, itemExiste));

        estudiantePropietarioFichaRule.validar(
                new PropiedadFicha(fichaDelItem, estudiante, esPropietario));

        itemSinRevisionesRule.validar(new RevisionesItem(item, totalRevisiones));
    }
}
