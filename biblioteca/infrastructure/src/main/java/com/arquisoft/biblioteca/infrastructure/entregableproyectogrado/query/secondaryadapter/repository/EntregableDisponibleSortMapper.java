package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;

import java.util.Map;

final class EntregableDisponibleSortMapper {

    private static final Map<String, String> RUTAS = Map.of(
            EntregableDisponibleCriteria.Campo.TITULO.getClave(), "tituloProyecto",
            EntregableDisponibleCriteria.Campo.TITULO_AUSENTE.getClave(), "tituloAusente",
            EntregableDisponibleCriteria.Campo.VERSION_ENTREGABLE.getClave(), "versionEntregable");

    private EntregableDisponibleSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
