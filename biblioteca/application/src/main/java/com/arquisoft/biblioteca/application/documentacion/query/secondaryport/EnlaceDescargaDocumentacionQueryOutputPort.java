package com.arquisoft.biblioteca.application.documentacion.query.secondaryport;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;

import java.util.UUID;

public interface EnlaceDescargaDocumentacionQueryOutputPort {

    EnlaceDescargaDocumentacionReadModel generar(
            UUID documentacion, String claveObjeto, String nombreArchivo);
}
