package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.application.observacionitem.command.validator.CerrarObservacionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.CierreObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemEnProgresoRule;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemExisteRule;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemEnProgresoRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemCorreccionDisponibleRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemCorreccionDisponibleRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CerrarObservacionItemValidatorImpl implements CerrarObservacionItemValidator {

    private final ObservacionItemExisteRule observacionItemExisteRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;
    private final RevisionItemCorreccionDisponibleRule revisionItemCorreccionDisponibleRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final ObservacionItemEnProgresoRule observacionItemEnProgresoRule;

    public CerrarObservacionItemValidatorImpl() {
        this.observacionItemExisteRule = new ObservacionItemExisteRuleImpl();
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
        this.revisionItemCorreccionDisponibleRule = new RevisionItemCorreccionDisponibleRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.observacionItemEnProgresoRule = new ObservacionItemEnProgresoRuleImpl();
    }

    @Override
    public void validar(CierreObservacionItemDomain entrada, ContextoObservacionItem contexto, FichaPerfilDomain ficha) {
        observacionItemExisteRule.validar(
                new ExistenciaObservacionItem(entrada.getObservacionItem(), !contexto.esVacio()));
        fichaPerfilExisteRule.validar(
                new ExistenciaFichaPerfil(contexto.fichaPerfil(), !ficha.esVacio()));

        var estadoRevision = new EstadoRevisionItem(contexto.revisionItem(), contexto.estadoRevision().getId());
        revisionItemNoCerradaRule.validar(estadoRevision);
        revisionItemCorreccionDisponibleRule.validar(estadoRevision);

        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(contexto.fichaPerfil(), contexto.asesorFicha(), entrada.getAsesorFicha()));
        observacionItemEnProgresoRule.validar(
                new EstadoObservacionItem(entrada.getObservacionItem(), contexto.estadoObservacion().getId()));
    }
}
