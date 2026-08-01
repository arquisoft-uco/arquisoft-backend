package com.arquisoft.fichas.domain.estudiantefichaperfil.rules;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.shared.rules.DomainRule;

import java.util.List;

public interface EstudianteFichaPerfilCupoDisponibleRule
        extends DomainRule<List<EstudianteFichaPerfilAggregate>> {
}
