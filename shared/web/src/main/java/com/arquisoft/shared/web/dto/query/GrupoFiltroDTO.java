package com.arquisoft.shared.web.dto.query;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrupoFiltroDTO implements NodoFiltroDTO {

    private FiltroConector conector;
    private List<NodoFiltroDTO> nodos;

    @Override
    public NodoFiltro toDomain() {
        if (conector == null) {
            throw new ApplicationException(
                    "El campo 'conector' es requerido en un nodo GRUPO",
                    "CONECTOR_REQUERIDO");
        }
        List<NodoFiltroDTO> lista = nodos != null ? nodos : List.of();
        return NodoFiltro.grupo(
                conector,
                lista.stream().map(NodoFiltroDTO::toDomain).toList()
        );
    }
}
