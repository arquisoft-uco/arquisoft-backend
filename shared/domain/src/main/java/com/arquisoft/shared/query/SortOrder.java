package com.arquisoft.shared.query;

import com.arquisoft.shared.exception.ApplicationException;
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

    /**
     * Parsea "campo" o "campo:ASC" / "campo:DESC".
     * Dirección inválida lanza ApplicationException (HTTP 400).
     */
    public static SortOrder parse(String expresion) {
        String[] partes = expresion.split(":", 2);
        String campo = partes[0].trim();
        if (campo.isBlank()) {
            throw new ApplicationException("El campo de ordenamiento no puede estar vacío", "SORT_CAMPO_VACIO");
        }
        SortDirection dir = SortDirection.ASC;
        if (partes.length > 1 && !partes[1].isBlank()) {
            try {
                dir = SortDirection.valueOf(partes[1].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(
                        "Dirección de ordenamiento inválida: '" + partes[1].trim() + "'. Use ASC o DESC",
                        "SORT_DIRECTION_INVALIDA");
            }
        }
        return new SortOrder(campo, dir);
    }

    public String getCampo()            { return campo; }
    public SortDirection getDireccion() { return direccion; }
}
