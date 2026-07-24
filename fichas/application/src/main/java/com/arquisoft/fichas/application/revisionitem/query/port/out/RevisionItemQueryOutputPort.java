package com.arquisoft.fichas.application.revisionitem.query.port.out;

import java.util.UUID;

public interface RevisionItemQueryOutputPort {

    long contarPorItem(UUID itemId);
}
