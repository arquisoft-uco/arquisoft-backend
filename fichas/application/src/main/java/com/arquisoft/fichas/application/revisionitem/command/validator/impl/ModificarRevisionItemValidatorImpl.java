package com.arquisoft.fichas.application.revisionitem.command.validator.impl;

import com.arquisoft.fichas.application.revisionitem.command.validator.ModificarRevisionItemValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaPropietarioRuleImpl;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;
import com.arquisoft.fichas.domain.revisionitem.rules.impl.RevisionItemExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModificarRevisionItemValidatorImpl implements ModificarRevisionItemValidator {

    private final RevisionItemExisteRule revisionItemExisteRule;
    private final AsesorFichaPropietarioRule asesorFichaPropietarioRule;

    public ModificarRevisionItemValidatorImpl() {
        this.revisionItemExisteRule = new RevisionItemExisteRuleImpl();
        this.asesorFichaPropietarioRule = new AsesorFichaPropietarioRuleImpl();
    }

    @Override
    public void validar(ModificacionRevisionItemDomain entrada, boolean revisionExiste, UUID fichaPerfil,
                         boolean esPropietario) {

        revisionItemExisteRule.validar(new ExistenciaRevisionItem(entrada.getItem(), revisionExiste));

        asesorFichaPropietarioRule.validar(
                new PropiedadAsesorFicha(fichaPerfil, entrada.getAsesorFicha(), esPropietario));
    }
}
