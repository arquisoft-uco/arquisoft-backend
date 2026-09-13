package com.arquisoft.shared.message.annotation;

/**
 * Textos de documentación OpenAPI del contexto {@code usuarios}.
 *
 * <p>Mismo criterio que {@link FichasApiMessages}: el texto va incrustado y no al catálogo de Redis,
 * porque la especificación OpenAPI se construye una vez al arrancar y no vuelve a consultarse. Ver
 * allí el razonamiento completo.
 */
public final class UsuariosApiMessages {

    private UsuariosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar esta acción";
    }

    public static final class Usuario {

        private Usuario() {}

        public static final String TAG_NAME = "Usuarios";
        public static final String TAG_DESCRIPTION = "Gestión de usuarios del sistema";

        public static final String REGISTRAR_SUMMARY = "Registrar información de un nuevo usuario";
        public static final String REGISTRAR_DESCRIPTION =
                "Registra un nuevo usuario en el proveedor de identidad (Keycloak) y persiste la "
                        + "información local con el identificador que Keycloak le asigna. Asigna los realm "
                        + "roles indicados y dispara el correo de establecimiento de contraseña. "
                        + "Exclusivo del rol administrador.";
        public static final String REGISTRAR_RESP_201 = "Usuario registrado — retorna el UUID asignado";
        public static final String REGISTRAR_RESP_400 = "Datos de entrada inválidos o rol no válido";
        public static final String REGISTRAR_RESP_422 = "Identificador, email o contacto ya registrados, o datos de dominio inválidos";
        public static final String REGISTRAR_RESP_503 = "No fue posible completar el registro; el servicio no está disponible temporalmente";
    }
}
