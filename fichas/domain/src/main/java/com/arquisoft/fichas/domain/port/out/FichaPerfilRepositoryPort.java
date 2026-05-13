package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

/**
 * Puerto de salida para el repositorio de fichas de perfil.
 *
 * <p>Contrato abstracto que debe implementar la capa de infraestructura.
 * Retorna entidades de dominio puras — nunca DTOs ni tipos Spring de ORM.</p>
 *
 * <p>Usa tipos propios del dominio ({@link PaginationRequest} y {@link PaginatedResult})
 * para mantener esta capa libre de dependencias de framework.</p>
 */
public interface FichaPerfilRepositoryPort {

    /**
     * Consulta todas las fichas de perfil de forma paginada.
     *
     * @param request criterios de paginación y ordenamiento
     * @return {@link PaginatedResult} con las fichas de dominio de la página solicitada
     */
    PaginatedResult<FichaPerfil> consultarTodas(PaginationRequest request);
}
