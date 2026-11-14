package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;

import java.util.Map;

final class DocumentacionSortMapper {

    private static final Map<String, String> RUTAS = Map.of(
            DocumentacionCriteria.Campo.FECHA_PUBLICACION.getClave(), "fechaPublicacion");

    private DocumentacionSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
