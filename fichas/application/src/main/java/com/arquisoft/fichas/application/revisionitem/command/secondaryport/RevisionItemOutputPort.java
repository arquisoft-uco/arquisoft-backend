package com.arquisoft.fichas.application.revisionitem.command.secondaryport;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;

import java.util.Optional;
import java.util.UUID;

public interface RevisionItemOutputPort {

    void registrarRevision(RevisionItemEntity revision);

    long contarPorItem(UUID itemId);

    Optional<RevisionItemEntity> buscarPorId(UUID revisionItemId);

    Optional<PertenenciaRevisionItemEntity> obtenerPertenencia(UUID revisionItem, UUID estudiante);

    void actualizarEstado(UUID revisionItem, String estadoActual, String estadoNuevo);
}
