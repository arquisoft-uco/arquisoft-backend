package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.query.FiltroOperador;
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
public class PredicadoMultivalorFiltroDTO implements NodoFiltroDTO {

    private String campo;
    private String operador;
    private List<String> valores;

    @Override
    public NodoFiltro toDomain() {
        return NodoFiltro.predicadoMultivalor(campo, FiltroOperador.parse(operador), valores);
    }
}
