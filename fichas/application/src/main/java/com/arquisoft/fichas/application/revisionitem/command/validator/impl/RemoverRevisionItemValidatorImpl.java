package com.arquisoft.fichas.application.revisionitem.command.validator.impl;

import com.arquisoft.fichas.application.revisionitem.command.validator.RemoverRevisionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoverRevisionItemValidatorImpl implements RemoverRevisionItemValidator {

    private final RevisionItemExisteRule revisionItemExisteRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;

    public RemoverRevisionItemValidatorImpl() {
        this.revisionItemExisteRule = new RevisionItemExisteRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
    }

    @Override
    public void validar(UUID revisionItem, UUID asesorSolicitante, UUID fichaPerfil, UUID asesorDeLaFicha,
                        boolean revisionExiste, String estadoRevisionId) {

        revisionItemExisteRule.validar(new ExistenciaRevisionItem(revisionItem, revisionExiste));

        asesorFichaPropietarioRule.validar(new PropiedadAsesorFicha(fichaPerfil, asesorDeLaFicha, asesorSolicitante));

        revisionItemNoCerradaRule.validar(new EstadoRevisionItem(revisionItem, estadoRevisionId));
    }
}
