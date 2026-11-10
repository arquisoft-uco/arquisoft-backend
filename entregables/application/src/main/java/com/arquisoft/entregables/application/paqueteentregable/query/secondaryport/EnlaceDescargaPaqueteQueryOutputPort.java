package com.arquisoft.entregables.application.paqueteentregable.query.secondaryport;

import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;

public interface EnlaceDescargaPaqueteQueryOutputPort {

    EnlaceDescargaPaqueteEntregableReadModel generar(String bucket, String claveObjeto, String nombreArchivo);
}
