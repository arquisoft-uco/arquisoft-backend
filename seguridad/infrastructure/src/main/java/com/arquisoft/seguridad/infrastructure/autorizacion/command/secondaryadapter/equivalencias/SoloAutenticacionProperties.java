package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@ConfigurationProperties("security.authorization.authenticated-only")
public record SoloAutenticacionProperties(List<Entrada> entries) {

    public SoloAutenticacionProperties {
        entries = UtilObjeto.esNulo(entries) ? List.of() : List.copyOf(entries);
        var vistas = new HashSet<String>();
        for (var entrada : entries) {
            if (!vistas.add(entrada.clave())) {
                throw new IllegalStateException("Fila duplicada en los recursos que solo exigen autenticacion: "
                        + entrada.clave());
            }
        }
    }

    public record Entrada(String method, String path) {

        public Entrada {
            if (UtilTexto.esVacioONulo(method) || UtilObjeto.esNulo(path) || !path.startsWith("/")) {
                throw new IllegalStateException("Fila invalida en los recursos que solo exigen autenticacion: "
                        + method + " " + path);
            }
            method = UtilTexto.aplicarTrim(method).toUpperCase(Locale.ROOT);
        }

        public String clave() {
            return method + " " + path;
        }
    }
}
