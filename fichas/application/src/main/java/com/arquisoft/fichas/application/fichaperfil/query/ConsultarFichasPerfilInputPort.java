package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

/**
 * Puerto de entrada para el caso de uso de consulta paginada de fichas de perfil.
 *
 * <p>Contrato que debe implementar la capa de aplicación.
 * Retorna un ReadModel — la proyección plana sin involucrar el aggregate de dominio.</p>
 *
 * <p>Usa tipos propios del dominio ({@link PaginationRequest} y {@link PaginatedResult})
 * para mantener esta capa libre de dependencias de framework.</p>
 */
public interface ConsultarFichasPerfilInputPort {

    /**
     * Retorna un listado paginado de todas las fichas de perfil registradas.
     *
     * @param request criterios de paginación y ordenamiento
     * @return {@link PaginatedResult} con las fichas de la página solicitada
     */
    PaginatedResult<FichaPerfilReadModel> ejecutar(PaginationRequest request);
}
