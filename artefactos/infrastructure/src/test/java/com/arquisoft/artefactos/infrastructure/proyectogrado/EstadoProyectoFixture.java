package com.arquisoft.artefactos.infrastructure.proyectogrado;

import java.time.Instant;

public final class EstadoProyectoFixture {

    private EstadoProyectoFixture() {}

    public static String estadoDe(Instant finalizadoEn, Instant canceladoEn) {
        if (canceladoEn != null) {
            return "CANCELADO";
        }
        if (finalizadoEn != null) {
            return "FINALIZADO";
        }
        return "EN_PROCESO";
    }

    public static Instant ocurridoEnDe(Instant finalizadoEn, Instant canceladoEn) {
        return canceladoEn != null ? canceladoEn : finalizadoEn;
    }
}
