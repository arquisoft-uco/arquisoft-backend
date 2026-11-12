package com.arquisoft.entregables.application.paqueteentregable.command.secondaryport;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;

public interface ArmadoPaqueteOutputPort {

    ArchivoPaqueteArmado armar(ContenidoPaqueteEntity contenido);

    void eliminar(String bucket, String claveObjeto);
}
