package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredicadoFiltroDTO implements NodoFiltroDTO {

    private String campo;
    private String operador;
    private String valor;

    @Override
    public NodoFiltro toDomain() {
        return NodoFiltro.predicado(campo, FiltroOperador.parse(operador), valor);
    }
}
