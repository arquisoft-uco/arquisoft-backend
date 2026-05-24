package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

/**
 * Criterio de consulta para fichas de perfil.
 *
 * Hereda paginación, ordenamiento y árbol de filtros de QueryCriteria.
 * Esta clase solo establece el tipo concreto y expone su builder.
 */
public final class FichaPerfilCriteria extends QueryCriteria {

    private FichaPerfilCriteria(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        public FichaPerfilCriteria build() {
            return new FichaPerfilCriteria(this);
        }
    }
}
