package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.UsuarioPorCorreoOutputPort;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.UsuarioPorCorreoWebClientOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Primary
@Component
public class UsuarioPorCorreoConCacheOutputAdapter implements UsuarioPorCorreoOutputPort {

    private static final String PREFIJO_CLAVE = "arquisoft:seguridad:usuario-por-correo:";

    private final UsuarioPorCorreoWebClientOutputPort delegado;
    private final StringRedisTemplate redis;
    private final UsuarioSesionProperties propiedades;
    private final AppLogger logger;

    public UsuarioPorCorreoConCacheOutputAdapter(UsuarioPorCorreoWebClientOutputPort delegado,
                                                 StringRedisTemplate redis,
                                                 UsuarioSesionProperties propiedades,
                                                 AppLogger logger) {
        this.delegado = delegado;
        this.redis = redis;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    @Override
    public Optional<UUID> obtenerIdPorCorreo(String correo) {
        if (!propiedades.cacheHabilitada()) {
            return delegado.obtenerIdPorCorreo(correo);
        }
        var clave = PREFIJO_CLAVE + ResolucionSesionConCacheOutputAdapter.huella(correo);
        var cacheado = leer(clave);
        logger.debug(ResolucionSesionKey.LOG_USUARIO_CACHE, cacheado.isPresent());
        if (cacheado.isPresent()) {
            return cacheado;
        }
        var encontrado = delegado.obtenerIdPorCorreo(correo);
        encontrado.ifPresent(usuario -> guardar(clave, usuario));
        return encontrado;
    }

    private Optional<UUID> leer(String clave) {
        try {
            var valor = redis.opsForValue().get(clave);
            return UtilObjeto.esNulo(valor)
                    ? Optional.empty()
                    : Optional.ofNullable(UtilUUID.generarUUIDDesdeTexto(valor));
        } catch (DataAccessException e) {
            logger.warn(ResolucionSesionKey.LOG_CACHE_USUARIO_DEGRADADA, e.getClass().getSimpleName());
            return Optional.empty();
        }
    }

    private void guardar(String clave, UUID usuario) {
        try {
            redis.opsForValue().set(clave, usuario.toString(), propiedades.cacheTtl());
        } catch (DataAccessException e) {
            logger.warn(ResolucionSesionKey.LOG_CACHE_USUARIO_DEGRADADA, e.getClass().getSimpleName());
        }
    }
}
