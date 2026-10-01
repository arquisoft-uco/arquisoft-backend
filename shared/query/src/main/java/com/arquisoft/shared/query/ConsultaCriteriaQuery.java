package com.arquisoft.shared.query;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public record ConsultaCriteriaQuery(
        int pagina,
        int tamanio,
        List<SortOrder> ordenamiento,
        NodoFiltro raiz
) {

    public static ConsultaCriteriaQuery crear(
            int pagina, int tamanio, List<SortOrder> ordenamiento, NodoFiltro raiz) {
        return new ConsultaCriteriaQuery(
                pagina, tamanio, UtilColeccion.aplicarPorDefecto(ordenamiento), raiz);
    }
}
