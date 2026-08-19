package com.arquisoft.shared.message.prueba;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.ContextosCatalogo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Catálogo para tests, resuelto contra los mismos archivos que carga el script de despliegue.
 *
 * <p>Los tests unitarios no levantan contexto de Spring, así que no hay Redis ni bean que inyectar;
 * y muchos comprueban el texto renderizado, no solo que se lance una excepción — que el nombre del
 * campo o el UUID lleguen al mensaje es comportamiento real y merece verificarse. El respaldo
 * genérico no puede sostener esas afirmaciones, así que aquí se lee el catálogo de verdad.
 *
 * <p>La fuente son los {@code catalogo/*.properties} de la raíz del repositorio, que Gradle copia a
 * los recursos de este artefacto de test fixtures. No hay segunda copia de los datos: el mismo
 * archivo que se carga en Redis es el que leen los tests, así que no pueden divergir.
 */
public final class CatalogoMensajesPrueba implements CatalogoMensajes {

    private static final String RUTA = "/catalogo/%s.properties";
    private static final String MARCADOR_AUSENTE = "??%s??";

    private static final CatalogoMensajesPrueba INSTANCIA = new CatalogoMensajesPrueba();

    private final Map<String, String> textos;

    private CatalogoMensajesPrueba() {
        this.textos = cargar();
    }

    /**
     * Devuelve la instancia compartida.
     *
     * @return el catálogo de prueba
     */
    public static CatalogoMensajesPrueba porDefecto() {
        return INSTANCIA;
    }

    @Override
    public String obtener(ClaveMensaje clave) {
        return textos.getOrDefault(clave.clave(), MARCADOR_AUSENTE.formatted(clave.clave()));
    }

    @Override
    public String formatear(ClaveMensaje clave, Object... args) {
        return obtener(clave).formatted(args);
    }

    @Override
    public boolean contiene(ClaveMensaje clave) {
        return textos.containsKey(clave.clave());
    }

    private static Map<String, String> cargar() {
        Map<String, String> acumulado = new HashMap<>();

        for (String contexto : ContextosCatalogo.TODOS) {
            String ruta = RUTA.formatted(contexto);

            try (InputStream entrada = CatalogoMensajesPrueba.class.getResourceAsStream(ruta)) {
                if (entrada == null) {
                    throw new IllegalStateException("No está en el classpath de test: " + ruta);
                }

                var propiedades = new Properties();
                propiedades.load(new InputStreamReader(entrada, StandardCharsets.UTF_8));
                propiedades.forEach((clave, texto) -> acumulado.put(clave.toString(), texto.toString()));
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo leer " + ruta, e);
            }
        }

        return Map.copyOf(acumulado);
    }
}
