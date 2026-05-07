package com.arquisoft.fichas.domain.port.in;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para el caso de uso de consulta paginada de fichas de perfil.
 *
 * <p>Contrato que debe implementar la capa de aplicación.
 * Retorna entidades de dominio — la conversión al DTO de respuesta ocurre en
 * la capa de infraestructura (controller) mediante {@code Page.map()}.
 *
 * <p>Usa {@link Page} y {@link Pageable} de spring-data-commons, librería ligera
 * de tipos de paginación que no arrastra Spring Boot ni ORM.
 */
public interface ConsultarFichasPerfilUseCase {

    /**
     * Retorna un listado paginado de todas las fichas de perfil registradas.
     *
     * @param pageable criterios de paginación y ordenamiento
     * @return {@link Page} con las fichas de la página solicitada
     */
    Page<FichaPerfil> ejecutar(Pageable pageable);
}
