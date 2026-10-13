package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.application.observacionitem.command.validator.RemoverObservacionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemExisteRule;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverObservacionItemValidatorImpl implements RemoverObservacionItemValidator {

    private final ObservacionItemExisteRule observacionItemExisteRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;

    public RemoverObservacionItemValidatorImpl() {
        this.observacionItemExisteRule = new ObservacionItemExisteRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
    }

    @Override
    public void validar(RemocionObservacionItemDomain entrada, ContextoObservacionItem contexto) {
        observacionItemExisteRule.validar(
                new ExistenciaObservacionItem(entrada.getObservacionItem(), !contexto.esVacio()));
        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(contexto.fichaPerfil(), contexto.asesorFicha(), entrada.getAsesorFicha()));
        revisionItemNoCerradaRule.validar(
                new EstadoRevisionItem(contexto.revisionItem(), contexto.estadoRevision().getId()));
    }
}
