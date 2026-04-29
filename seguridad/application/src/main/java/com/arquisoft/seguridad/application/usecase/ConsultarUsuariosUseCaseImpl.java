package com.arquisoft.seguridad.application.usecase;

import com.arquisoft.seguridad.application.dto.PaginaResponseDTO;
import com.arquisoft.seguridad.application.dto.UsuarioFiltroDTO;
import com.arquisoft.seguridad.application.dto.UsuarioResponseDTO;
import com.arquisoft.seguridad.domain.exception.ParametroFiltroInvalidoException;
import com.arquisoft.seguridad.domain.model.EstadoUsuario;
import com.arquisoft.seguridad.domain.model.Usuario;
import com.arquisoft.seguridad.domain.model.UsuarioRole;
import com.arquisoft.seguridad.domain.port.in.ConsultarUsuariosUseCase;
import com.arquisoft.seguridad.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del caso de uso para consultar usuarios con filtros y paginación.
 *
 * <p>Orquesta el flujo de consulta:
 * <ol>
 *   <li>Valida y convierte los parámetros de filtro string a tipos del dominio (POL-04).</li>
 *   <li>Delega la consulta en {@link UsuarioRepositoryPort}.</li>
 *   <li>Mapea los resultados de dominio a DTOs de respuesta.</li>
 *   <li>Construye y retorna la respuesta paginada.</li>
 * </ol>
 *
 * <p><b>Regla de negocio Usuario-POL-04:</b> los datos enviados como filtro deben ser
 * válidos a nivel de tipo de dato, longitud, obligatoriedad, formato y rango.
 *
 * <p><b>Nota de arquitectura:</b> {@code @Transactional} vive en el adaptador de repositorio
 * (infrastructure), no en el use case. El use case orquesta lógica de negocio pura.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultarUsuariosUseCaseImpl implements ConsultarUsuariosUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    /**
     * {@inheritDoc}
     *
     * <p>Flujo:
     * <ol>
     *   <li>Convierte {@code filtro.estado()} a {@link EstadoUsuario} — lanza
     *       {@link ParametroFiltroInvalidoException} si el valor es inválido.</li>
     *   <li>Convierte {@code filtro.rol()} a {@link UsuarioRole} usando
     *       {@code UsuarioRole.fromCode()} — lanza {@link ParametroFiltroInvalidoException}
     *       si el código no existe.</li>
     *   <li>Llama {@code buscarConFiltros} y {@code contarConFiltros} en el repositorio.</li>
     *   <li>Mapea cada {@link Usuario} a {@link UsuarioResponseDTO#fromDomain(Usuario)}.</li>
     *   <li>Retorna {@link PaginaResponseDTO#fromData(long, int, int, List)}.</li>
     * </ol>
     */
    @Override
    public List<Usuario> buscarUsuarios(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol,
            int pagina,
            int tamano) {
        log.debug("Consultando usuarios — nombreOEmail={}, estado={}, rol={}, pagina={}, tamano={}",
                nombreOEmail, estado, rol, pagina, tamano);
        return usuarioRepositoryPort.buscarConFiltros(nombreOEmail, estado, rol, pagina, tamano);
    }

    @Override
    public long contarUsuarios(
            String nombreOEmail,
            EstadoUsuario estado,
            UsuarioRole rol) {
        return usuarioRepositoryPort.contarConFiltros(nombreOEmail, estado, rol);
    }

    /**
     * Convierte el string de filtro de estado al enum {@link EstadoUsuario}.
     * Aplica la regla POL-04: el valor debe ser exactamente {@code "ACTIVO"} o {@code "INACTIVO"}.
     *
     * @param estadoStr valor string del filtro; puede ser {@code null}
     * @return {@link EstadoUsuario} correspondiente, o {@code null} si el string es nulo/vacío
     * @throws ParametroFiltroInvalidoException si el valor no corresponde a ningún estado válido
     */
    public EstadoUsuario convertirEstado(String estadoStr) {
        if (estadoStr == null || estadoStr.isBlank()) {
            return null;
        }
        try {
            return EstadoUsuario.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Filtro de estado inválido recibido: '{}'", estadoStr);
            throw new ParametroFiltroInvalidoException(
                    "FILTRO_ESTADO_INVALIDO",
                    "El valor de estado '" + estadoStr + "' no es válido. "
                            + "Los valores permitidos son: ACTIVO, INACTIVO");
        }
    }

    /**
     * Convierte el string de filtro de rol al enum {@link UsuarioRole}.
     * Aplica la regla POL-04: el código debe coincidir exactamente con un valor de {@link UsuarioRole}.
     *
     * @param rolStr valor string del filtro; puede ser {@code null}
     * @return {@link UsuarioRole} correspondiente, o {@code null} si el string es nulo/vacío
     * @throws ParametroFiltroInvalidoException si el código no corresponde a ningún rol válido
     */
    public UsuarioRole convertirRol(String rolStr) {
        if (rolStr == null || rolStr.isBlank()) {
            return null;
        }
        try {
            return UsuarioRole.fromCode(rolStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Filtro de rol inválido recibido: '{}'", rolStr);
            throw new ParametroFiltroInvalidoException(
                    "FILTRO_ROL_INVALIDO",
                    "El valor de rol '" + rolStr + "' no es válido. "
                            + "Los valores permitidos son: ESTUDIANTE, ASESOR, ASESOR_FICHA, "
                            + "COORDINADOR, JURADO, BIBLIOTECARIO, ADMINISTRADOR, "
                            + "REPRESENTANTE_COMITE_CURRICULUM");
        }
    }

    /**
     * Método de orquestación completo: valida filtros, consulta el repositorio,
     * mapea resultados y construye la respuesta paginada.
     *
     * <p>Este método es el punto de entrada principal que el Controller invoca.
     *
     * @param filtro DTO con los parámetros de filtro y paginación
     * @return página de resultados con metadatos y lista de {@link UsuarioResponseDTO}
     */
    public PaginaResponseDTO<UsuarioResponseDTO> ejecutar(UsuarioFiltroDTO filtro) {
        EstadoUsuario estado = convertirEstado(filtro.estado());
        UsuarioRole rol = convertirRol(filtro.rol());

        List<Usuario> usuarios = buscarUsuarios(
                filtro.nombreOEmail(), estado, rol, filtro.pagina(), filtro.tamano());
        long total = contarUsuarios(filtro.nombreOEmail(), estado, rol);

        List<UsuarioResponseDTO> dtos = usuarios.stream()
                .map(UsuarioResponseDTO::fromDomain)
                .toList();

        return PaginaResponseDTO.fromData(total, filtro.pagina(), filtro.tamano(), dtos);
    }
}
