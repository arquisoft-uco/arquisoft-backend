package com.arquisoft.shared.query;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.pagination.SortDirection;

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
        String[] partes = expresion.split(":", 2);
        String campo = partes[0].trim();
        if (campo.isBlank()) {
            throw new FiltroException(
                    Mensajes.obtener(ConsultaKey.ERROR_ORDEN_CAMPO_VACIO),
                    AppCodes.Consulta.SORT_CAMPO_VACIO);
        }
        SortDirection dir = SortDirection.ASC;
        if (partes.length > 1 && !partes[1].isBlank()) {
            try {
                dir = SortDirection.valueOf(partes[1].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new FiltroException(
                        Mensajes.formatear(ConsultaKey.ERROR_ORDEN_DIRECCION_INVALIDA, partes[1].trim()),
                        AppCodes.Consulta.SORT_DIRECTION_INVALIDA);
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
