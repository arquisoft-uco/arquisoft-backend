package com.arquisoft.shared.pagination;

import com.arquisoft.shared.message.key.app.PaginacionKey;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

public final class PaginationRequest {

    private final int page;
    private final int size;
    private final String sort;
    private final SortDirection direction;

    private PaginationRequest(int page, int size, String sort, SortDirection direction) {
        if (size <= 0) {
            throw new ApplicationException(
                    Mensajes.obtener(PaginacionKey.SIZE_MAYOR_CERO), AppCodes.Paginacion.SIZE_INVALIDA);
        }
        this.page = Math.max(0, page);
        this.size = size;
        this.sort = sort;
        this.direction = (!UtilObjeto.esNulo(direction)) ? direction : SortDirection.ASC;
    }

    public static PaginationRequest of(int page, int size) {
        return new PaginationRequest(page, size, null, SortDirection.ASC);
    }

    public static PaginationRequest of(int page, int size, String sort, SortDirection direction) {
        return new PaginationRequest(page, size, sort, direction);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public boolean hasSort() {
        return !UtilTexto.esVacioONulo(sort);
    }
}
