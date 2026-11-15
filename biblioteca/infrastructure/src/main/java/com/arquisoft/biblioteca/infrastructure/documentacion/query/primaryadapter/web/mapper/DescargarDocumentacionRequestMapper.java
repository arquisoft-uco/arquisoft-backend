package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.UUID;

public final class DescargarDocumentacionRequestMapper {

    private DescargarDocumentacionRequestMapper() {}

    public static DescargarDocumentacionQuery toQuery(UUID documentacion, IdentidadActual identidad) {
        return DescargarDocumentacionQuery.crear(
                documentacion,
                RolesIdentidad.traducir(identidad, ConsultarDocumentacionesRequestMapper.ROLES_NEGOCIO_POR_ROL_IDENTIDAD));
    }
}
