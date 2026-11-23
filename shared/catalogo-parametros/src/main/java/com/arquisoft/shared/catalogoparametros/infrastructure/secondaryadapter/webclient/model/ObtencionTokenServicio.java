package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.model;

public sealed interface ObtencionTokenServicio {

    record Vigente(String accessToken) implements ObtencionTokenServicio {}

    record Rechazada(int estado) implements ObtencionTokenServicio {}

    record NoDisponible(String motivo) implements ObtencionTokenServicio {}
}
