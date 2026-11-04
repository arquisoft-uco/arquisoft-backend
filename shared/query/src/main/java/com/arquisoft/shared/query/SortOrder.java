package com.arquisoft.shared.query;

import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilTexto;

public final class SortOrder {

    private final String campo;
    private final SortDirection direccion;

    private SortOrder(String campo, SortDirection direccion) {
        this.campo = campo;
        this.direccion = direccion;
    }

    public static SortOrder of(String campo, SortDirection direccion) {
        return new SortOrder(campo, direccion != null ? direccion : SortDirection.ASC);
    }

    public static SortOrder parse(String expresion) {
        if (UtilTexto.esVacioONulo(expresion)) {
            throw new FiltroException(
                    AppCodes.Consulta.SORT_CAMPO_VACIO,
                    ConsultaKey.ERROR_ORDEN_CAMPO_VACIO, ConsultaKey.ERROR_ORDEN_CAMPO_VACIO_DETALLE);
        }
        String[] partes = expresion.split(":", 2);
        String campo = partes[0].trim();
        if (campo.isBlank()) {
            throw new FiltroException(
                    AppCodes.Consulta.SORT_CAMPO_VACIO,
                    ConsultaKey.ERROR_ORDEN_CAMPO_VACIO, ConsultaKey.ERROR_ORDEN_CAMPO_VACIO_DETALLE);
        }
        var dir = SortDirection.ASC;
        if (partes.length > 1 && !partes[1].isBlank()) {
            try {
                dir = SortDirection.valueOf(partes[1].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new FiltroException(
                        AppCodes.Consulta.SORT_DIRECTION_INVALIDA,
                        ConsultaKey.ERROR_ORDEN_DIRECCION_INVALIDA, ConsultaKey.ERROR_ORDEN_DIRECCION_INVALIDA_DETALLE,
                        partes[1].trim());
            }
        }
        return new SortOrder(campo, dir);
    }

    public String getCampo() {
        return campo;
    }

    public SortDirection getDireccion() {
        return direccion;
    }
}
