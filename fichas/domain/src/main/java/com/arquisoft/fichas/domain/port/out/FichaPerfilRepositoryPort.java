package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de salida para el repositorio de fichas de perfil.
 *
 * <p>Contrato abstracto que debe implementar la capa de infraestructura.
 * Retorna entidades de dominio puras — nunca DTOs ni tipos Spring de ORM.
 *
 * <p>Usa {@link Page} y {@link Pageable} de spring-data-commons, librería ligera
 * de tipos de paginación que no arrastra Spring Boot ni ORM.
 */
public interface FichaPerfilRepositoryPort {

    /**
     * Consulta todas las fichas de perfil de forma paginada.
     *
     * @param pageable criterios de paginación y ordenamiento
     * @return {@link Page} con las fichas de dominio de la página solicitada
     */
    Page<FichaPerfil> consultarTodas(Pageable pageable);
}
