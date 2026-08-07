package com.arquisoft.shared.message;

import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.shared.message.annotation.ValidationKeys;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Red de seguridad del catálogo.
 *
 * <p>Al pasar los textos a {@code .properties} se pierde la verificación del compilador: una clave
 * mal escrita ya no rompe el build, solo degrada el mensaje en tiempo de ejecución. Estas pruebas
 * devuelven esa garantía — cierran el hueco que abre la externalización.
 *
 * <p>Los enums de {@code key.*} se descubren escaneando el paquete, no listándolos a mano: una
 * lista manual reabre el mismo agujero que este test cierra, porque un enum nuevo sin registrar
 * quedaría sin verificar y nadie se enteraría.
 */
class CatalogoMensajesClavesTest {

    private static final String PAQUETE_CLAVES = "com/arquisoft/shared/message/key";

    private final CatalogoMensajesResourceBundle catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @Test
    @DisplayName("el escaneo encuentra los enums de claves — si fallara, el resto pasaría en vacío")
    void debeEncontrarLosEnumsDeClaves_cuandoSeEscaneaElPaquete() {
        assertThat(clavesDeclaradas())
                .as("sin claves descubiertas las demás pruebas serían tautológicas")
                .isNotEmpty();
    }

    @Test
    @DisplayName("toda clave declarada resuelve a un texto de su propio bundle")
    void debeResolverTodaClaveDeclarada_cuandoSeRecorrenLosEnums() {
        List<String> sinTexto = new ArrayList<>();

        for (ClaveMensaje clave : clavesDeclaradas()) {
            if (!catalogo.contiene(clave)) {
                sinTexto.add(clave.clave() + " (bundle declarado: " + clave.paquete() + ")");
            }
        }

        assertThat(sinTexto)
                .as("claves declaradas en Java que su bundle no contiene")
                .isEmpty();
    }

    @Test
    @DisplayName("toda clave de ValidationKeys resuelve contra ValidationMessages.properties")
    void debeResolverTodaClaveDeValidacion_cuandoSeRecorreValidationKeys() {
        List<String> sinTexto = new ArrayList<>();

        for (String referencia : constantesDe(ValidationKeys.class)) {
            String clave = ValidationKeys.sinLlaves(referencia);
            if (!catalogo.contieneClave(clave)) {
                sinTexto.add(clave);
            }
        }

        assertThat(sinTexto).isEmpty();
    }

    @Test
    @DisplayName("toda clave de FichasApiKeys existe en el bundle de documentación")
    void debeResolverTodaClaveDeApi_cuandoSeRecorreFichasApiKeys() {
        ResourceBundle apiDocs = ResourceBundle.getBundle(PaquetesMensajes.FICHAS_API, Locale.ROOT);
        List<String> sinTexto = new ArrayList<>();

        for (String referencia : constantesDe(FichasApiKeys.class)) {
            // TAG_NAME/TAG_DESCRIPTION llevan el texto incrustado — springdoc no resuelve
            // ${...} en @Tag. Solo se verifican las que sí son referencias.
            if (!esReferenciaDeApi(referencia)) {
                continue;
            }
            String clave = claveDeApi(referencia);
            if (!apiDocs.containsKey(clave)) {
                sinTexto.add(clave);
            }
        }

        assertThat(sinTexto).isEmpty();
    }

    @Test
    @DisplayName("ningún texto del catálogo queda huérfano — todo .properties tiene su clave")
    void debeTenerClave_cuandoSeRecorrenLasClavesDelBundle() {
        Set<String> declaradas = new LinkedHashSet<>();
        clavesDeclaradas().forEach(clave -> declaradas.add(clave.clave()));
        for (String referencia : constantesDe(ValidationKeys.class)) {
            declaradas.add(ValidationKeys.sinLlaves(referencia));
        }
        for (String referencia : constantesDe(FichasApiKeys.class)) {
            if (esReferenciaDeApi(referencia)) {
                declaradas.add(claveDeApi(referencia));
            }
        }

        List<String> huerfanas = catalogo.claves().stream()
                .filter(clave -> !declaradas.contains(clave))
                .toList();

        assertThat(huerfanas)
                .as("textos definidos en .properties que ninguna clave referencia")
                .isEmpty();
    }

    @Test
    @DisplayName("ninguna clave está declarada en más de un bundle")
    void debeNoDeclararClaveEnDosBundles_cuandoSeComparanTodos() {
        Map<String, List<String>> bundlesPorClave = new LinkedHashMap<>();
        for (String baseName : PaquetesMensajes.TODOS) {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    baseName, CatalogoMensajesResourceBundle.LOCALE_POR_DEFECTO);
            bundle.keySet().forEach(clave ->
                    bundlesPorClave.computeIfAbsent(clave, k -> new ArrayList<>()).add(baseName));
        }

        List<String> duplicadas = bundlesPorClave.entrySet().stream()
                .filter(entrada -> entrada.getValue().size() > 1)
                .map(entrada -> entrada.getKey() + " → " + entrada.getValue())
                .toList();

        assertThat(duplicadas)
                .as("una clave en dos bundles la resuelve el primero de PaquetesMensajes.TODOS, "
                        + "en silencio y sin que el otro texto se use nunca")
                .isEmpty();
    }

    @Test
    @DisplayName("una clave inexistente devuelve un marcador visible en lugar de lanzar")
    void debeDevolverMarcador_cuandoLaClaveNoExiste() {
        ClaveMensaje inexistente = claveDePrueba(
                "fichas.dominio.fichaperfil.error.no-existe", PaquetesMensajes.FICHAS);

        String resultado = catalogo.obtener(inexistente);

        assertThat(CatalogoMensajesResourceBundle.esMarcaDeClaveAusente(resultado, inexistente.clave()))
                .isTrue();
        assertThat(catalogo.contiene(inexistente)).isFalse();
    }

    @Test
    @DisplayName("una clave que declara el bundle equivocado no se busca en los demás")
    void debeDevolverMarcador_cuandoElBundleDeclaradoNoLaContiene() {
        // El texto existe, pero en messages/fichas.properties, no en el bundle que la clave declara.
        ClaveMensaje bundleErroneo = claveDePrueba(
                FichaPerfilKey.ERROR_TITULO_DUPLICADO.clave(), PaquetesMensajes.SEGURIDAD);

        String resultado = catalogo.obtener(bundleErroneo);

        assertThat(CatalogoMensajesResourceBundle.esMarcaDeClaveAusente(resultado, bundleErroneo.clave()))
                .as("resolver por búsqueda ocultaría que la clave declara mal su bundle")
                .isTrue();
    }

    @Test
    @DisplayName("formatear sustituye los parámetros del patrón")
    void debeSustituirParametros_cuandoElPatronLosDeclara() {
        String resultado = catalogo.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, "Mi proyecto");

        assertThat(resultado).isEqualTo("El título ya existe: Mi proyecto");
    }

    @Test
    @DisplayName("los patrones de log conservan los marcadores {} de SLF4J")
    void debeConservarMarcadoresSlf4j_cuandoSeObtieneUnPatronDeLog() {
        String patron = catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA);

        assertThat(patron).contains("{}");
    }

    // -------------------------------------------------------------------------

    private static ClaveMensaje claveDePrueba(String clave, String bundle) {
        return new ClaveMensaje() {
            @Override
            public String clave() {
                return clave;
            }

            @Override
            public String paquete() {
                return bundle;
            }
        };
    }

    private static boolean esReferenciaDeApi(String valor) {
        return valor.startsWith("${") && valor.endsWith("}");
    }

    private static String claveDeApi(String referencia) {
        return referencia.substring(2, referencia.length() - 1);
    }

    /**
     * Descubre todas las constantes de los enums de {@code key.*} escaneando el directorio de
     * clases compiladas. Evita una lista manual, que dejaría sin cubrir cualquier enum nuevo.
     */
    private List<ClaveMensaje> clavesDeclaradas() {
        List<ClaveMensaje> claves = new ArrayList<>();
        for (Class<?> tipo : enumsDeClaves()) {
            for (Object constante : tipo.getEnumConstants()) {
                claves.add((ClaveMensaje) constante);
            }
        }
        return claves;
    }

    private List<Class<?>> enumsDeClaves() {
        URL raiz = ClaveMensaje.class.getClassLoader().getResource(PAQUETE_CLAVES);
        assertThat(raiz).as("no se encontró el paquete de claves en el classpath").isNotNull();

        Path directorio;
        try {
            directorio = Path.of(raiz.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(PAQUETE_CLAVES, e);
        }

        try (Stream<Path> archivos = Files.walk(directorio)) {
            return archivos
                    .filter(ruta -> ruta.getFileName().toString().endsWith("Key.class"))
                    .map(ruta -> nombreDeClase(directorio, ruta))
                    .map(CatalogoMensajesClavesTest::cargar)
                    .filter(Class::isEnum)
                    .filter(ClaveMensaje.class::isAssignableFrom)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException(directorio.toString(), e);
        }
    }

    private static String nombreDeClase(Path directorio, Path archivo) {
        String relativa = directorio.relativize(archivo).toString()
                .replace('\\', '/')
                .replaceAll("\\.class$", "");
        return PAQUETE_CLAVES.replace('/', '.') + "." + relativa.replace('/', '.');
    }

    private static Class<?> cargar(String nombre) {
        try {
            return Class.forName(nombre);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(nombre, e);
        }
    }

    /** Recolecta el valor de las constantes {@code String} de la clase y de sus clases anidadas. */
    private List<String> constantesDe(Class<?> clase) {
        List<String> valores = new ArrayList<>();
        for (Class<?> anidada : clase.getDeclaredClasses()) {
            valores.addAll(constantesDe(anidada));
        }
        for (Field campo : clase.getDeclaredFields()) {
            if (!Modifier.isStatic(campo.getModifiers())
                    || !Modifier.isPublic(campo.getModifiers())
                    || campo.getType() != String.class) {
                continue;
            }
            try {
                valores.add((String) campo.get(null));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(campo.getName(), e);
            }
        }
        return valores;
    }
}
