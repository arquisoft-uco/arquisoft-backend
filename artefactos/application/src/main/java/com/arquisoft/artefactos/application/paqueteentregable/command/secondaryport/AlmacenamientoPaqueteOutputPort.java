package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ContenidoPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;

public interface AlmacenamientoPaqueteOutputPort {

    ArchivoPaqueteArmado armar(ContenidoPaqueteEntity contenido);
}
