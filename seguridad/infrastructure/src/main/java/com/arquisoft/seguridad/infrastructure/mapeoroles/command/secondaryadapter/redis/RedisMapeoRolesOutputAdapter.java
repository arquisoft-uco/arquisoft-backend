package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.MapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.RolClienteOtorgadoOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.mapper.IndiceRolesClienteMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RedisMapeoRolesOutputAdapter implements MapeoRolesOutputPort, RolClienteOtorgadoOutputPort {

    private static final String PREFIJO = "arquisoft:authz";
    private static final String CLAVE_PUNTERO = PREFIJO + ":generacion";
    private static final String CLAVE_VIGENTE = PREFIJO + ":vigente";
    private static final String CLAVE_RESERVA = PREFIJO + ":lock";
    private static final String PLANTILLA_INDICE = PREFIJO + ":gen:%s:client-role:";
    private static final String VALOR_MARCA = "1";
    private static final long CLAVES_DE_VIGENCIA = 2L;
    private static final long SIN_MAPEO = -1L;
    private static final long OTORGADO = 1L;

    private static final RedisScript<Long> SCRIPT_DECISION =
            RedisScript.of(new ClassPathResource("redis/seguridad/rol-cliente-otorgado.lua"), Long.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final MapeoRolesProperties propiedades;
    private final AppLogger logger;

    public RedisMapeoRolesOutputAdapter(StringRedisTemplate stringRedisTemplate, MapeoRolesProperties propiedades,
                                        AppLogger logger) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    @Override
    public boolean estaVigente() {
        try {
            var existentes = stringRedisTemplate.countExistingKeys(List.of(CLAVE_VIGENTE, CLAVE_PUNTERO));
            return UtilObjeto.noEsNulo(existentes) && existentes == CLAVES_DE_VIGENCIA;
        } catch (DataAccessException e) {
            throw almacenNoDisponible(e);
        }
    }

    @Override
    public boolean reservarActualizacion() {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                    .setIfAbsent(CLAVE_RESERVA, VALOR_MARCA, propiedades.lockLease()));
        } catch (DataAccessException e) {
            throw almacenNoDisponible(e);
        }
    }

    @Override
    public void guardar(MapeoRolesEntity mapeo) {
        var indice = IndiceRolesClienteMapper.toIndice(mapeo);
        var generacion = UtilUUID.generarNuevoUUID().toString();
        var ttlIndice = propiedades.ttl().plus(propiedades.lockLease());
        try {
            escribirIndice(indice, generacion, ttlIndice);
            stringRedisTemplate.opsForValue().set(CLAVE_PUNTERO, generacion, propiedades.ttl());
            stringRedisTemplate.opsForValue().set(CLAVE_VIGENTE, VALOR_MARCA, propiedades.refreshInterval());
        } catch (DataAccessException e) {
            throw almacenNoDisponible(e);
        }
        logger.debug(MapeoRolesKey.LOG_MAPEO_GUARDADO, indice.size(), mapeo.clientRolesPorRolRealm().size());
    }

    @Override
    public boolean rolClienteOtorgadoPor(String rolCliente, Collection<String> rolesRealm) {
        if (UtilColeccion.esVaciaONula(rolesRealm)) {
            return false;
        }
        var argumentos = new ArrayList<Object>();
        argumentos.add(PLANTILLA_INDICE);
        argumentos.add(rolCliente);
        argumentos.addAll(rolesRealm);

        Long resultado;
        try {
            resultado = stringRedisTemplate.execute(SCRIPT_DECISION, List.of(CLAVE_PUNTERO), argumentos.toArray());
        } catch (DataAccessException e) {
            throw almacenNoDisponible(e);
        }
        if (UtilObjeto.esNulo(resultado) || resultado == SIN_MAPEO) {
            logger.error(MapeoRolesKey.LOG_SIN_MAPEO);
            throw new ProveedorAutorizacionNoDisponibleException(
                    Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE));
        }
        return resultado == OTORGADO;
    }

    private void escribirIndice(Map<String, Set<String>> indice, String generacion, Duration ttlIndice) {
        var serializador = stringRedisTemplate.getStringSerializer();
        RedisCallback<Object> escritura = conexion -> {
            indice.forEach((rolCliente, rolesRealm) -> {
                var clave = serializador.serialize(PLANTILLA_INDICE.formatted(generacion) + rolCliente);
                var miembros = rolesRealm.stream().map(serializador::serialize).toArray(byte[][]::new);
                conexion.setCommands().sAdd(clave, miembros);
                conexion.keyCommands().expire(clave, ttlIndice.toSeconds());
            });
            return null;
        };
        stringRedisTemplate.executePipelined(escritura);
    }

    private ProveedorAutorizacionNoDisponibleException almacenNoDisponible(DataAccessException causa) {
        logger.error(MapeoRolesKey.LOG_ALMACEN_NO_DISPONIBLE, causa.getMessage());
        return new ProveedorAutorizacionNoDisponibleException(
                Mensajes.obtener(AutorizacionKey.ERROR_PROVEEDOR_NO_DISPONIBLE), causa);
    }
}
