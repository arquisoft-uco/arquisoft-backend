package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PredicadoFiltroDTO.class, name = "PREDICADO"),
        @JsonSubTypes.Type(value = GrupoFiltroDTO.class,     name = "GRUPO")
})
public interface NodoFiltroDTO {

    NodoFiltro toDomain();
}
