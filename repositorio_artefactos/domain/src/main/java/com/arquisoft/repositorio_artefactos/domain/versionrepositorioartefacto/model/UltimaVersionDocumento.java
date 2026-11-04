package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilTexto;

public record UltimaVersionDocumento(String nombreDocumento, int numeroVersion) {

    public static final UltimaVersionDocumento VACIO = new UltimaVersionDocumento(UtilTexto.VACIO, UtilNumero.CERO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
