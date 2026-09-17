package com.arquisoft.fichas.application.observacionitem.command.validator.impl;

import com.arquisoft.fichas.application.observacionitem.command.validator.AgregarObservacionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.model.DisponibilidadObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemNoDuplicadaRule;
import com.arquisoft.fichas.domain.observacionitem.rules.impl.ObservacionItemNoDuplicadaRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarObservacionItemValidatorImpl implements AgregarObservacionItemValidator {

    private final RevisionItemExisteRule revisionItemExisteRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;
    private final ObservacionItemNoDuplicadaRule observacionItemNoDuplicadaRule;

    public AgregarObservacionItemValidatorImpl() {
        this.revisionItemExisteRule = new RevisionItemExisteRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
        this.observacionItemNoDuplicadaRule = new ObservacionItemNoDuplicadaRuleImpl();
    }

    @Override
    public void validar(AgregacionObservacionItemDomain entrada, boolean revisionExiste,
                        String estadoRevisionId, UUID fichaPerfil, UUID asesorDeLaFicha,
                        long observacionesIguales) {
        revisionItemExisteRule.validar(new ExistenciaRevisionItem(entrada.getRevisionItem(), revisionExiste));
        revisionItemNoCerradaRule.validar(new EstadoRevisionItem(entrada.getRevisionItem(), estadoRevisionId));
        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(fichaPerfil, asesorDeLaFicha, entrada.getAsesorFicha()));
        observacionItemNoDuplicadaRule.validar(new DisponibilidadObservacionItem(
                entrada.getRevisionItem(), entrada.getObservacion(), observacionesIguales));
    }
}
