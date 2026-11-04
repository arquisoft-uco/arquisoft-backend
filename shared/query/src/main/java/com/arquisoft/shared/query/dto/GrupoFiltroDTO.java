package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilTexto;
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

    private String conector;
    private List<NodoFiltroDTO> nodos;

    @Override
    public NodoFiltro toDomain() {
        if (UtilTexto.esVacioONulo(conector)) {
            throw new FiltroException(
                    AppCodes.Consulta.CONECTOR_REQUERIDO,
                    ConsultaKey.ERROR_CONECTOR_REQUERIDO, ConsultaKey.ERROR_CONECTOR_REQUERIDO_DETALLE);
        }
        return NodoFiltro.grupo(
                FiltroConector.parse(conector),
                UtilColeccion.aplicarPorDefecto(nodos).stream()
                        .map(NodoFiltroDTO::toDomain)
                        .toList()
        );
    }
}
