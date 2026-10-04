package com.arquisoft.usuarios.application.bibliotecario.query.primaryport.mapper;

import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarBibliotecariosAdministradorMapper {

    private ConsultarBibliotecariosAdministradorMapper() {}

    public static BibliotecarioCriteria toCriteria(ConsultaCriteriaQuery query) {
        return BibliotecarioCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
