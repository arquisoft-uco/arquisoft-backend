package com.arquisoft.fichas.domain.port.in;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.shared.domain.Page;


/**
 * Puerto de entrada para el caso de uso de consulta paginada de fichas de perfil.
 *
 * <p>Contrato que debe implementar la capa de aplicación.
 * Retorna entidades de dominio — nunca DTOs.
 * Java puro: sin Spring, sin Lombok, sin JPA.
 */
public interface ConsultarFichasPerfilUseCase {

    /**
     * Retorna un listado paginado de todas las fichas de perfil registradas.
     *
     * @param page  número de página solicitada (base 0)
     * @param size cantidad de elementos por página
     * @return {@link Page} con las fichas de la página solicitada
     */
    Page<FichaPerfil> ejecutar(int page, int size);
}

