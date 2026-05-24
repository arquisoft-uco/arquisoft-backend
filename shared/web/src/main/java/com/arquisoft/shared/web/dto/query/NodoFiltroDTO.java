package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Contrato de deserialización JSON para el árbol de filtros.
 *
 * El discriminador "tipo" en el JSON determina qué implementación concreta
 * instancia Jackson: PREDICADO → PredicadoFiltroDTO, GRUPO → GrupoFiltroDTO.
 *
 * ISP: interfaz mínima con un único método de conversión al modelo de dominio.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PredicadoFiltroDTO.class, name = "PREDICADO"),
        @JsonSubTypes.Type(value = GrupoFiltroDTO.class,     name = "GRUPO")
})
public interface NodoFiltroDTO {

    NodoFiltro toDomain();
}
