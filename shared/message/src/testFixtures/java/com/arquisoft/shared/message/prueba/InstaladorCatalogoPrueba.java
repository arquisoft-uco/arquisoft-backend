package com.arquisoft.shared.message.prueba;

import com.arquisoft.shared.message.Mensajes;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * Instala el catálogo de prueba en la fachada estática al abrir la sesión de tests.
 *
 * <p>La capa de dominio resuelve sus textos por {@code Mensajes}, que no tiene punto de inyección:
 * sus agregados y excepciones se construyen con factorías estáticas, nunca como beans. Sin esto,
 * cada test que comprueba un mensaje de dominio recibiría el texto genérico del respaldo.
 *
 * <p>Se registra por {@code ServiceLoader}, que el JUnit Platform descubre por sí solo — sin
 * anotación en cada clase de test y sin activar la autodetección global de extensiones, que
 * registraría también las que traen otras librerías del classpath de test.
 */
public class InstaladorCatalogoPrueba implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        Mensajes.instalar(CatalogoMensajesPrueba.porDefecto());
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        Mensajes.instalar(null);
    }
}
