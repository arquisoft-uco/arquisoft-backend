package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemExisteRule;

public class RevisionItemExisteRuleImpl implements RevisionItemExisteRule {

    @Override
    public void validar(ExistenciaRevisionItem existencia) {
        if (!existencia.existe()) {
            throw new RevisionItemNoEncontradoException(existencia.revisionItem());
        }
    }
}
