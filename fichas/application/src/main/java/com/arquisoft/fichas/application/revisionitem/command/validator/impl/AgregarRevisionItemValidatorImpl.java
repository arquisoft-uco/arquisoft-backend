package com.arquisoft.fichas.application.revisionitem.command.validator.impl;

import com.arquisoft.fichas.application.revisionitem.command.validator.AgregarRevisionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.model.DisponibilidadRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoDuplicadaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoDuplicadaRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarRevisionItemValidatorImpl implements AgregarRevisionItemValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final RevisionItemNoDuplicadaRule revisionItemNoDuplicadaRule;

    public AgregarRevisionItemValidatorImpl() {
        this.itemFichaPerfilExisteRule = new ItemFichaPerfilExisteRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.revisionItemNoDuplicadaRule = new RevisionItemNoDuplicadaRuleImpl();
    }

    @Override
    public void validar(AgregacionRevisionItemDomain entrada, boolean itemExiste, UUID fichaPerfil,
                         UUID asesorFicha, long cantidadRevisiones) {

        itemFichaPerfilExisteRule.validar(new ExistenciaItemFichaPerfil(entrada.getItem(), itemExiste));

        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(fichaPerfil, asesorFicha, entrada.getAsesorFicha()));

        revisionItemNoDuplicadaRule.validar(
                new DisponibilidadRevisionItem(entrada.getItem(), cantidadRevisiones));
    }
}
