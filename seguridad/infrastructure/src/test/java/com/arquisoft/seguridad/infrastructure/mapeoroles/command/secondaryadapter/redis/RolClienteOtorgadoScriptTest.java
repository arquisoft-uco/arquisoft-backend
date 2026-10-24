package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RolClienteOtorgadoScriptTest {

    private static final String RECURSO_SCRIPT = "/redis/seguridad/rol-cliente-otorgado.lua";
    private static final String CLAVE_PUNTERO = "arquisoft:authz:generacion";
    private static final String PLANTILLA = "arquisoft:authz:gen:%s:client-role:";

    private final Map<String, String> cadenas = new HashMap<>();
    private final Map<String, Set<String>> conjuntos = new HashMap<>();
    private String script;

    @BeforeEach
    void preparar() throws IOException {
        try (var entrada = getClass().getResourceAsStream(RECURSO_SCRIPT)) {
            script = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private int ejecutar(String rolCliente, String... rolesRealm) {
        var globals = JsePlatform.standardGlobals();
        globals.set("unpack", globals.get("table").get("unpack"));
        globals.set("redis", redisSimulado());

        var keys = new LuaTable();
        keys.set(1, LuaValue.valueOf(CLAVE_PUNTERO));
        var argv = new LuaTable();
        argv.set(1, LuaValue.valueOf(PLANTILLA));
        argv.set(2, LuaValue.valueOf(rolCliente));
        for (var i = 0; i < rolesRealm.length; i++) {
            argv.set(i + 3, LuaValue.valueOf(rolesRealm[i]));
        }
        globals.set("KEYS", keys);
        globals.set("ARGV", argv);

        return globals.load(script, "rol-cliente-otorgado").call().toint();
    }

    private LuaTable redisSimulado() {
        var redis = new LuaTable();
        redis.set("call", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                var comando = args.arg1().tojstring();
                var clave = args.arg(2).tojstring();
                return switch (comando) {
                    case "GET" -> cadenas.containsKey(clave) ? LuaValue.valueOf(cadenas.get(clave)) : LuaValue.FALSE;
                    case "SMISMEMBER" -> pertenencia(clave, args);
                    default -> throw new IllegalStateException("comando no simulado: " + comando);
                };
            }
        });
        return redis;
    }

    private LuaTable pertenencia(String clave, Varargs args) {
        var miembros = conjuntos.getOrDefault(clave, Set.of());
        var resultado = new LuaTable();
        for (var i = 3; i <= args.narg(); i++) {
            resultado.set(i - 2, LuaValue.valueOf(miembros.contains(args.arg(i).tojstring()) ? 1 : 0));
        }
        return resultado;
    }

    @Test
    void debeDevolverMenosUno_cuandoNoHayPuntero() {
        // Act / Assert
        assertThat(ejecutar("fichas:ver", "asesor")).isEqualTo(-1);
    }

    @Test
    void debeDevolverUno_cuandoAlgunRolEsMiembro() {
        // Arrange
        cadenas.put(CLAVE_PUNTERO, "g1");
        conjuntos.put("arquisoft:authz:gen:g1:client-role:fichas:ver", Set.of("asesor"));

        // Act / Assert
        assertThat(ejecutar("fichas:ver", "estudiante", "asesor")).isEqualTo(1);
    }

    @Test
    void debeDevolverCero_cuandoNingunoEsMiembro() {
        // Arrange
        cadenas.put(CLAVE_PUNTERO, "g1");
        conjuntos.put("arquisoft:authz:gen:g1:client-role:fichas:ver", Set.of("asesor"));

        // Act / Assert
        assertThat(ejecutar("fichas:ver", "estudiante", "jurado")).isEqualTo(0);
    }

    @Test
    void debeDevolverCero_cuandoLaClaveDelIndiceNoExiste() {
        // Arrange
        cadenas.put(CLAVE_PUNTERO, "g1");

        // Act / Assert
        assertThat(ejecutar("fichas:borrar", "asesor")).isEqualTo(0);
    }

    @Test
    void debeLeerLaGeneracionDelPuntero() {
        // Arrange
        conjuntos.put("arquisoft:authz:gen:g1:client-role:fichas:ver", Set.of("asesor"));
        conjuntos.put("arquisoft:authz:gen:g2:client-role:fichas:ver", Set.of("estudiante"));

        // Act / Assert
        cadenas.put(CLAVE_PUNTERO, "g1");
        assertThat(ejecutar("fichas:ver", "asesor")).isEqualTo(1);
        assertThat(ejecutar("fichas:ver", "estudiante")).isEqualTo(0);
        cadenas.put(CLAVE_PUNTERO, "g2");
        assertThat(ejecutar("fichas:ver", "asesor")).isEqualTo(0);
        assertThat(ejecutar("fichas:ver", "estudiante")).isEqualTo(1);
    }
}
