package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.application.observacionitem.command.validator.ReabrirObservacionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemCerradaRule;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemExisteRule;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemCerradaRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ReabrirObservacionItemValidatorImpl implements ReabrirObservacionItemValidator {

    private final ObservacionItemExisteRule observacionItemExisteRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final ObservacionItemCerradaRule observacionItemCerradaRule;

    public ReabrirObservacionItemValidatorImpl() {
        this.observacionItemExisteRule = new ObservacionItemExisteRuleImpl();
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.observacionItemCerradaRule = new ObservacionItemCerradaRuleImpl();
    }

    @Override
    public void validar(ReaperturaObservacionItemDomain entrada, ContextoObservacionItem contexto, FichaPerfilDomain ficha) {
        observacionItemExisteRule.validar(
                new ExistenciaObservacionItem(entrada.getObservacionItem(), !contexto.esVacio()));
        fichaPerfilExisteRule.validar(
                new ExistenciaFichaPerfil(contexto.fichaPerfil(), !ficha.esVacio()));

        revisionItemNoCerradaRule.validar(
                new EstadoRevisionItem(contexto.revisionItem(), contexto.estadoRevision().getId()));

        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(contexto.fichaPerfil(), contexto.asesorFicha(), entrada.getAsesorFicha()));
        observacionItemCerradaRule.validar(
                new EstadoObservacionItem(entrada.getObservacionItem(), contexto.estadoObservacion().getId()));
    }
}
