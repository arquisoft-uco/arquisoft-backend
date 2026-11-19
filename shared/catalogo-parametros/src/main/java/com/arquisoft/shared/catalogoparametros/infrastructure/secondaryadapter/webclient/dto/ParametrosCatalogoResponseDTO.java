package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParametrosCatalogoResponseDTO(List<ParametroItem> parametros) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ParametroItem(Parametro parametro, List<MetadatoItem> metadatos) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parametro(String nombre, Boolean activo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MetadatoItem(Metadato metadato) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadato(Object valor) {}
}
