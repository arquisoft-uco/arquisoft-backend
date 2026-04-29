package com.arquisoft.seguridad.domain.port.out;

import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;

import java.util.List;

/**
 * Puerto de salida (driven port) para la persistencia de usuarios.
 *
 * <p>Define el contrato de acceso a datos que la capa {@code infrastructure}
 * debe implementar. Solo usa tipos del dominio — ninguna dependencia de JPA,
 * Spring Data ni ningún framework externo.
 *
 * <p>La implementación concreta vive en
 * {@code infrastructure/adapter/out/persistence/UsuarioRepositoryAdapter}.
 */
public interface UsuarioRepositoryPort {

    /**
     * Busca usuarios que cumplan los filtros indicados y retorna la página solicitada.
     *
     * @param nombreOEmail búsqueda parcial (LIKE, case-insensitive) sobre nombre, apellido o email;
     *                     {@code null} si no se aplica este filtro
     * @param estado       filtro exacto por estado del usuario; {@code null} si no se aplica
     * @param rol          filtro por rol contextual asignado; {@code null} si no se aplica
     * @param pagina       número de página (0-indexed)
     * @param tamano       cantidad máxima de registros por página
     * @return lista de usuarios de la página solicitada (puede ser vacía)
     */
    List<Usuario> buscarConFiltros(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol,
            int pagina,
            int tamano);

    /**
     * Cuenta el total de usuarios que cumplen los filtros indicados.
     * Se usa para construir el header {@code X-Total-Count} de la respuesta paginada.
     *
     * @param nombreOEmail búsqueda parcial sobre nombre, apellido o email; {@code null} si no aplica
     * @param estado       filtro exacto por estado; {@code null} si no aplica
     * @param rol          filtro por rol contextual; {@code null} si no aplica
     * @return total de registros que cumplen los filtros
     */
    long contarConFiltros(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol);
}
