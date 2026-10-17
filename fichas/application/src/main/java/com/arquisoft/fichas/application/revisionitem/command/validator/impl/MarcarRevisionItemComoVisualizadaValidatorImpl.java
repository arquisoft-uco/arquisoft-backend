package com.arquisoft.fichas.application.revisionitem.command.validator.impl;

import com.arquisoft.fichas.application.revisionitem.command.validator.MarcarRevisionItemComoVisualizadaValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantePropietarioFichaRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemExisteRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemNoCerradaRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MarcarRevisionItemComoVisualizadaValidatorImpl implements MarcarRevisionItemComoVisualizadaValidator {

    private final RevisionItemExisteRule revisionItemExisteRule;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final RevisionItemNoCerradaRule revisionItemNoCerradaRule;

    public MarcarRevisionItemComoVisualizadaValidatorImpl() {
        this.revisionItemExisteRule = new RevisionItemExisteRuleImpl();
        this.estudiantePropietarioFichaRule = new EstudiantePropietarioFichaRuleImpl();
        this.revisionItemNoCerradaRule = new RevisionItemNoCerradaRuleImpl();
    }

    @Override
    public void validar(UUID revisionItem, UUID estudiante, UUID fichaPerfil, boolean revisionExiste,
                        boolean esPropietario, String estadoRevisionId) {

        revisionItemExisteRule.validar(new ExistenciaRevisionItem(revisionItem, revisionExiste));

        estudiantePropietarioFichaRule.validar(new PropiedadFicha(fichaPerfil, estudiante, esPropietario));

        revisionItemNoCerradaRule.validar(new EstadoRevisionItem(revisionItem, estadoRevisionId));
    }
}
