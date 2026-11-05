package com.arquisoft.seguridad.infrastructure.config.cors;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class OrigenesPermitidos {

    private static final String SEPARADOR_LISTA = ",";
    private static final String BARRA_FINAL = "/+$";

    private final List<String> origenes;

    public OrigenesPermitidos(
            @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:4200,"
                    + "http://localhost:5173,http://localhost:5174}") String origenes) {
        this.origenes = Arrays.stream(UtilObjeto.aplicarPorDefecto(origenes, UtilTexto.VACIO).split(SEPARADOR_LISTA))
                .map(OrigenesPermitidos::normalizar)
                .filter(origen -> !origen.isEmpty())
                .toList();
    }

    public boolean contiene(String origen) {
        return UtilObjeto.noEsNulo(origen) && origenes.contains(normalizar(origen));
    }

    public List<String> lista() {
        return origenes;
    }

    public static String normalizar(String origen) {
        return UtilTexto.aplicarTrim(origen).replaceAll(BARRA_FINAL, UtilTexto.VACIO).toLowerCase(Locale.ROOT);
    }
}
