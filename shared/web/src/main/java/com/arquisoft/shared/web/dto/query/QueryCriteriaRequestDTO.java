package com.arquisoft.shared.web.dto.query;

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

    /**
     * Devuelve la solicitud recibida, o una con los valores por defecto si el cuerpo vino vacío.
     *
     * <p>Los endpoints de consulta declaran {@code @RequestBody(required = false)}, así que el
     * mapper recibe {@code null} cuando el cliente no envía cuerpo. Resolverlo aquí evita repetir
     * el mismo ternario en cada {@code RequestMapper}: la política de «sin cuerpo equivale a la
     * primera página con el tamaño por defecto» se decide una sola vez, junto a los campos cuyos
     * valores por defecto la definen.
     *
     * @param solicitud solicitud deserializada, posiblemente nula
     * @return la misma solicitud, o una nueva con los valores por defecto
     */
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
