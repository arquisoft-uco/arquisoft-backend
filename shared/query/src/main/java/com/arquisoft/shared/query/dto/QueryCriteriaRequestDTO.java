package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
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

    public static QueryCriteriaRequestDTO aplicarPorDefecto(QueryCriteriaRequestDTO solicitud) {
        return UtilObjeto.aplicarPorDefecto(solicitud, new QueryCriteriaRequestDTO());
    }

    public List<SortOrder> parsearOrdenamiento() {
        if (UtilColeccion.esVaciaONula(ordenamiento)) {
            return List.of();
        }
        return ordenamiento.stream().map(SortOrder::parse).toList();
    }

    public NodoFiltro parsearFiltros() {
        return UtilObjeto.esNulo(filtros) ? null : filtros.toDomain();
    }
}
