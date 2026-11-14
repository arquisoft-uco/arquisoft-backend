package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;

public interface ArmadoPaqueteOutputPort {

    ArchivoPaqueteArmado armar(ContenidoPaqueteEntity contenido);

    void eliminar(String bucket, String claveObjeto);

    void eliminarTrasConfirmar(String bucket, String claveObjeto);

    void eliminarSiRevierte(String bucket, String claveObjeto);
}
