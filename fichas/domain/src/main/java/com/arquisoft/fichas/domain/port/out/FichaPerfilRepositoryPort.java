package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.shared.domain.Page;

/**
 * Puerto de salida para el repositorio de fichas de perfil.
 *
 * <p>Contrato abstracto que debe implementar la capa de infraestructura.
 * Retorna entidades de dominio puras — nunca DTOs ni tipos Spring.
 */
public interface FichaPerfilRepositoryPort {

    /**
     * Consulta todas las fichas de perfil de forma paginada.
     *
     * @param page número de página solicitada (base 0)
     * @param size cantidad de elementos por página
     * @return {@link Page} con las fichas de dominio de la página solicitada
     */
    Page<FichaPerfil> consultarPaginadas(int page, int size);
}

