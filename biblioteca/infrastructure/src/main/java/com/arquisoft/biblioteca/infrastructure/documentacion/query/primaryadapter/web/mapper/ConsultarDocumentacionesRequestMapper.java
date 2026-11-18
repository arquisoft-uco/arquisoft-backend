package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.RolesIdentidad;

import java.util.Map;

public final class ConsultarDocumentacionesRequestMapper {

    static final Map<String, RolConsultaDocumentacion> ROLES_NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.BIBLIOTECARIO, RolConsultaDocumentacion.BIBLIOTECARIO,
            UsuariosRoles.ASESOR, RolConsultaDocumentacion.ASESOR,
            UsuariosRoles.COORDINADOR, RolConsultaDocumentacion.COORDINADOR,
            UsuariosRoles.ESTUDIANTE, RolConsultaDocumentacion.ESTUDIANTE);

    private ConsultarDocumentacionesRequestMapper() {}

    public static ConsultarDocumentacionesQuery toQuery(QueryCriteriaRequestDTO dto, IdentidadActual identidad) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarDocumentacionesQuery.crear(
                RolesIdentidad.traducir(identidad, ROLES_NEGOCIO_POR_ROL_IDENTIDAD), criterio);
    }
}
