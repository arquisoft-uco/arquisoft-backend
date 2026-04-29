package com.arquisoft.seguridad.domain.port.in;

import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;

import java.util.List;

/**
 * Puerto de entrada (driving port) para la consulta de usuarios con filtros y paginación.
 *
 * <p>Define el contrato que el Controller REST invoca. La implementación concreta
 * vive en {@code application/usecase/ConsultarUsuariosUseCaseImpl}.
 *
 * <p><b>Arquitectura hexagonal estricta:</b> este puerto usa únicamente tipos del dominio
 * y primitivos Java. El use case impl (en application) adapta los DTOs antes de invocar
 * este puerto y convierte los resultados a DTOs de respuesta.
 */
public interface ConsultarUsuariosUseCase {

    /**
     * Busca usuarios que cumplan los filtros indicados y retorna la página solicitada.
     *
     * @param nombreOEmail búsqueda parcial sobre nombre, apellido o email; {@code null} si no aplica
     * @param estado       filtro por estado del usuario; {@code null} si no aplica
     * @param rol          filtro por rol contextual; {@code null} si no aplica
     * @param pagina       número de página (0-indexed)
     * @param tamano       tamaño de página (1-100)
     * @return lista de usuarios de la página solicitada (puede ser vacía)
     * @throws com.arquisoft.seguridad.domain.exception.ParametroFiltroInvalidoException
     *         si algún parámetro de filtro tiene un valor inválido (POL-04)
     */
    List<Usuario> buscarUsuarios(
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
     * @param estado       filtro por estado del usuario; {@code null} si no aplica
     * @param rol          filtro por rol contextual; {@code null} si no aplica
     * @return total de registros que cumplen los filtros
     */
    long contarUsuarios(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol);
}
