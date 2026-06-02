package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryCriteriaRequestDTO {

    private int pagina   = 0;
    private int tamanio  = 10;
    private List<String> ordenamiento;
    private NodoFiltroDTO filtros;

    public List<SortOrder> parsearOrdenamiento() {
        if (ordenamiento == null || ordenamiento.isEmpty()) {
            return List.of();
        }
        return ordenamiento.stream().map(SortOrder::parse).toList();
    }

    public NodoFiltro parsearFiltros() {
        return filtros != null ? filtros.toDomain() : null;
    }
}
