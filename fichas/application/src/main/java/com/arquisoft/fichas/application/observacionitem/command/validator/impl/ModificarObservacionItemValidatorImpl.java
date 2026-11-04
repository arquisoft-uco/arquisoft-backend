package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.application.observacionitem.command.validator.ModificarObservacionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.DisponibilidadObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemExisteRule;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemNoDuplicadaRule;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemNoDuplicadaRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarObservacionItemValidatorImpl implements ModificarObservacionItemValidator {

    private final ObservacionItemExisteRule observacionItemExisteRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final ObservacionItemNoDuplicadaRule observacionItemNoDuplicadaRule;

    public ModificarObservacionItemValidatorImpl() {
        this.observacionItemExisteRule = new ObservacionItemExisteRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.observacionItemNoDuplicadaRule = new ObservacionItemNoDuplicadaRuleImpl();
    }

    @Override
    public void validar(ModificacionObservacionItemDomain entrada, ContextoObservacionItem contexto,
                        long observacionesIguales) {
        observacionItemExisteRule.validar(
                new ExistenciaObservacionItem(entrada.getObservacionItem(), !contexto.esVacio()));
        revisionItemNoCerradaRule.validar(
                new EstadoRevisionItem(contexto.revisionItem(), contexto.estadoRevision().getId()));
        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(contexto.fichaPerfil(), contexto.asesorFicha(), entrada.getAsesorFicha()));
        observacionItemNoDuplicadaRule.validar(new DisponibilidadObservacionItem(
                contexto.revisionItem(), entrada.getObservacion(), observacionesIguales));
    }
}
