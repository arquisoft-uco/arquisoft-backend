package com.arquisoft.shared.pagination;

import com.arquisoft.shared.message.AppCodes;
import com.arquisoft.shared.message.AppKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.util.UtilObject;
import com.arquisoft.shared.util.UtilText;

public final class PaginationRequest {

    private final int page;
    private final int size;
    private final String sort;
    private final SortDirection direction;

    private PaginationRequest(int page, int size, String sort, SortDirection direction) {
        if (size <= 0) {
            throw new ApplicationException(
                    Messages.obtener(AppKeys.Paginacion.SIZE_MAYOR_CERO), AppCodes.Paginacion.SIZE_INVALIDA);
        }
        this.page = Math.max(0, page);
        this.size = size;
        this.sort = sort;
        this.direction = (!UtilObject.isNull(direction)) ? direction : SortDirection.ASC;
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
        return !UtilText.isEmptyOrNull(sort);
    }
}
