package com.arquisoft.biblioteca.application.documentacion.query.secondaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity.ArchivoDocumentacionAccesoEntity;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

public final class ArchivoDocumentacionAccesoMapper {

    private ArchivoDocumentacionAccesoMapper() {}

    public static ArchivoDocumentacion toDomain(ArchivoDocumentacionAccesoEntity entity) {
        return new ArchivoDocumentacion(
                entity.documentacion(),
                EstadoDocumentacion.desde(entity.estado()),
                UtilObjeto.aplicarPorDefecto(entity.claveObjeto(), UtilTexto.VACIO),
                UtilObjeto.aplicarPorDefecto(entity.nombreArchivo(), UtilTexto.VACIO));
    }
}
