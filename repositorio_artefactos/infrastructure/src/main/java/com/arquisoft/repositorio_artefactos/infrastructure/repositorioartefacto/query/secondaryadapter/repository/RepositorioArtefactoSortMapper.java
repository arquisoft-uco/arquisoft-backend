package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RepositorioArtefactoSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (var campo : RepositorioArtefactoCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case NOMBRE_REPOSITORIO -> "nombreRepositorio";
                case DESCRIPCION -> "descripcion";
                case ADMINISTRADOR_NOMBRE -> "administradorNombre";
                case ADMINISTRADOR_ID -> null;
                case ADMINISTRADOR_IDENTIFICADOR -> null;
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private RepositorioArtefactoSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
