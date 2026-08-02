package com.arquisoft.shared.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Red de seguridad del catálogo.
 *
 * <p>Al pasar los textos a {@code .properties} se pierde la verificación del compilador: una clave
 * mal escrita ya no rompe el build, solo degrada el mensaje en tiempo de ejecución. Estas pruebas
 * devuelven esa garantía — cierran el hueco que abre la externalización.
 */
class MessageCatalogClavesTest {

    private static final List<Class<?>> CLASES_DE_CLAVES =
            List.of(AppKeys.class, FichasKeys.class, SeguridadKeys.class, UsuariosKeys.class);

    private final ResourceBundleMessageCatalog catalogo = ResourceBundleMessageCatalog.porDefecto();

    @Test
    @DisplayName("toda clave declarada en las clases *Keys resuelve a un texto del bundle")
    void debeResolverTodaClaveDeclarada_cuandoSeRecorrenLasClasesDeClaves() {
        List<String> sinTexto = new ArrayList<>();

        for (String clave : clavesDeclaradas()) {
            if (!catalogo.contiene(clave)) {
                sinTexto.add(clave);
            }
        }

        assertThat(sinTexto)
                .as("claves declaradas en Java que no existen en ningún .properties")
                .isEmpty();
    }

    @Test
    @DisplayName("toda clave de ValidationKeys resuelve contra ValidationMessages.properties")
    void debeResolverTodaClaveDeValidacion_cuandoSeRecorreValidationKeys() {
        List<String> sinTexto = new ArrayList<>();

        for (String referencia : constantesDe(ValidationKeys.class)) {
            String clave = ValidationKeys.sinLlaves(referencia);
            if (!catalogo.contiene(clave)) {
                sinTexto.add(clave);
            }
        }

        assertThat(sinTexto).isEmpty();
    }

    @Test
    @DisplayName("toda clave de FichasApiKeys existe en el bundle de documentación")
    void debeResolverTodaClaveDeApi_cuandoSeRecorreFichasApiKeys() {
        ResourceBundle apiDocs = ResourceBundle.getBundle(MessageBundles.FICHAS_API, Locale.ROOT);
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
    @DisplayName("ningún texto del catálogo queda huérfano — todo .properties tiene su constante")
    void debeTenerConstante_cuandoSeRecorrenLasClavesDelBundle() {
        Set<String> declaradas = new LinkedHashSet<>(clavesDeclaradas());
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
                .as("textos definidos en .properties que ninguna constante referencia")
                .isEmpty();
    }

    @Test
    @DisplayName("una clave inexistente devuelve un marcador visible en lugar de lanzar")
    void debeDevolverMarcador_cuandoLaClaveNoExiste() {
        String inexistente = "fichas.dominio.fichaperfil.error.no-existe";

        String resultado = catalogo.obtener(inexistente);

        assertThat(ResourceBundleMessageCatalog.esMarcaDeClaveAusente(resultado, inexistente)).isTrue();
        assertThat(catalogo.contiene(inexistente)).isFalse();
    }

    @Test
    @DisplayName("formatear sustituye los parámetros del patrón")
    void debeSustituirParametros_cuandoElPatronLosDeclara() {
        String resultado = catalogo.formatear(FichasKeys.FichaPerfil.ERROR_TITULO_DUPLICADO, "Mi proyecto");

        assertThat(resultado).isEqualTo("El título ya existe: Mi proyecto");
    }

    @Test
    @DisplayName("los patrones de log conservan los marcadores {} de SLF4J")
    void debeConservarMarcadoresSlf4j_cuandoSeObtieneUnPatronDeLog() {
        String patron = catalogo.obtener(FichasKeys.FichaPerfil.LOG_REGISTRADA);

        assertThat(patron).contains("{}");
    }

    // -------------------------------------------------------------------------

    private static boolean esReferenciaDeApi(String valor) {
        return valor.startsWith("${") && valor.endsWith("}");
    }

    private static String claveDeApi(String referencia) {
        return referencia.substring(2, referencia.length() - 1);
    }

    private List<String> clavesDeclaradas() {
        List<String> claves = new ArrayList<>();
        CLASES_DE_CLAVES.forEach(clase -> claves.addAll(constantesDe(clase)));
        return claves;
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
