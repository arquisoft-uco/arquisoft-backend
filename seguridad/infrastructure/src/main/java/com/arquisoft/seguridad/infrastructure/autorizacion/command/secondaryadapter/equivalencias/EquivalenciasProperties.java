package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@ConfigurationProperties("security.authorization.role-table")
public record EquivalenciasProperties(List<Entrada> entries) {

    public EquivalenciasProperties {
        entries = UtilObjeto.esNulo(entries) ? List.of() : List.copyOf(entries);
        var vistas = new HashSet<String>();
        for (var entrada : entries) {
            if (!vistas.add(entrada.clave())) {
                throw new IllegalStateException("Fila duplicada en la tabla de equivalencias: " + entrada.clave());
            }
        }
    }

    public record Entrada(String method, String path, String role) {

        public Entrada {
            if (UtilTexto.esVacioONulo(method) || UtilObjeto.esNulo(path) || !path.startsWith("/")
                    || UtilTexto.esVacioONulo(role)) {
                throw new IllegalStateException("Fila invalida en la tabla de equivalencias: "
                        + method + " " + path + " " + role);
            }
            method = UtilTexto.aplicarTrim(method).toUpperCase(Locale.ROOT);
        }

        public String clave() {
            return method + " " + path;
        }
    }
}
