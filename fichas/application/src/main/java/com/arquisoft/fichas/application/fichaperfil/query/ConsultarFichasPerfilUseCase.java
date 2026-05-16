package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilResponseDTO;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

/**
 * Puerto de entrada para el caso de uso de consulta paginada de fichas de perfil.
 *
 * <p>Contrato que debe implementar la capa de aplicación.
 * Retorna entidades de dominio — la conversión al DTO de respuesta ocurre en
 * la capa de infraestructura (controller) mediante {@code PaginatedResult.map()}.</p>
 *
 * <p>Usa tipos propios del dominio ({@link PaginationRequest} y {@link PaginatedResult})
 * para mantener esta capa libre de dependencias de framework.</p>
 */
public interface ConsultarFichasPerfilUseCase {

    /**
     * Retorna un listado paginado de todas las fichas de perfil registradas.
     *
     * @param request criterios de paginación y ordenamiento
     * @return {@link PaginatedResult} con las fichas de la página solicitada
     */
    PaginatedResult<FichaPerfilResponseDTO> ejecutar(PaginationRequest request);
}
