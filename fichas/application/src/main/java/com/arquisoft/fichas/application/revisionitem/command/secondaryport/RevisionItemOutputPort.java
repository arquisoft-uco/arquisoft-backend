package com.arquisoft.fichas.application.revisionitem.command.secondaryport;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;

import java.util.UUID;

public interface RevisionItemOutputPort {

    void registrarRevision(RevisionItemEntity revision);

    void actualizarEstado(UUID itemId, String estadoRevision);

    long contarPorItem(UUID itemId);
}
