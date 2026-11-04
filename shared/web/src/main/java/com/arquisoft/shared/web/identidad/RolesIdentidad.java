package com.arquisoft.shared.web.identidad;

import java.util.List;
import java.util.Map;

public final class RolesIdentidad {

    private RolesIdentidad() {}

    public static <T> List<T> traducir(IdentidadActual identidad, Map<String, T> rolPorRolIdentidad) {
        return identidad.roles().stream()
                .filter(rolPorRolIdentidad::containsKey)
                .map(rolPorRolIdentidad::get)
                .distinct()
                .toList();
    }
}
