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

        public static final String MODIFICAR_SUMMARY = "Modificar información de un usuario existente";
        public static final String MODIFICAR_DESCRIPTION =
                "Modifica los datos personales enviados de un usuario activo y sincroniza el email y el "
                        + "nombre en el proveedor de identidad (Keycloak). Los roles enviados se agregan o, si "
                        + "fueron eliminados lógicamente, se reactivan; un rol ausente de la lista no se revoca. "
                        + "Solo cambian los campos presentes en el body. Exclusivo del rol administrador.";
        public static final String MODIFICAR_RESP_204 = "Usuario modificado";
        public static final String MODIFICAR_RESP_400 =
                "Identificador de usuario inválido, rol no válido o body sin ningún dato ni rol";
        public static final String MODIFICAR_RESP_422 =
                "Usuario inexistente o inactivo, datos ya usados por otro usuario, o rol ya vigente";
        public static final String MODIFICAR_RESP_503 =
                "No fue posible sincronizar con el proveedor de identidad; el servicio no está disponible temporalmente";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String REMOVER_SUMMARY = "Remover información de un estudiante";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol estudiante de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role estudiante en Keycloak y notifica a los contextos "
                        + "que replican al estudiante. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Estudiante removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol estudiante vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_ADMINISTRADOR_SUMMARY =
                "Consultar información de los estudiantes (administrador)";
        public static final String CONSULTAR_ADMINISTRADOR_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de todos los estudiantes, incluidos los dados de baja. "
                        + "Cada fila indica el estado del usuario y si el rol estudiante sigue vigente. "
                        + "Exclusivo del rol administrador.";
        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los estudiantes vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los estudiantes con el rol vigente. No expone el "
                        + "estado del usuario ni la vigencia. Disponible para asesores, asesores de ficha, "
                        + "coordinadores y representantes del comité.";
        public static final String CONSULTAR_RESP_200 = "Página de estudiantes";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String REMOVER_SUMMARY = "Remover información de un asesor";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol asesor de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role asesor en Keycloak y notifica a los contextos "
                        + "que replican al asesor. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Asesor removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol asesor vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_ADMINISTRADOR_SUMMARY =
                "Consultar información de los asesores (administrador)";
        public static final String CONSULTAR_ADMINISTRADOR_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de todos los asesores, incluidos los dados de baja. "
                        + "Cada fila indica el estado del usuario y si el rol asesor sigue vigente. "
                        + "Exclusivo del rol administrador.";
        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los asesores vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los asesores con el rol vigente. No expone el "
                        + "estado del usuario ni la vigencia. Disponible para asesores, coordinadores y asesores de ficha.";
        public static final String CONSULTAR_RESP_200 = "Página de asesores";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class AsesorFicha {

        private AsesorFicha() {}

        public static final String REMOVER_SUMMARY = "Remover información de un asesor de ficha";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol asesor de ficha de un usuario: marca la fecha de eliminación sin "
                        + "borrar la fila, revoca el realm role asesor-ficha en Keycloak y notifica a los "
                        + "contextos que replican al asesor de ficha. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Asesor de ficha removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol asesor de ficha vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_ADMINISTRADOR_SUMMARY =
                "Consultar información de los asesores de ficha (administrador)";
        public static final String CONSULTAR_ADMINISTRADOR_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de todos los asesores de ficha, incluidos los dados de "
                        + "baja. Cada fila indica el estado del usuario y si el rol asesor de ficha sigue vigente. "
                        + "Exclusivo del rol administrador.";
        public static final String CONSULTAR_VIGENTES_SUMMARY =
                "Consultar información de los asesores de ficha vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los asesores de ficha con el rol vigente. Expone y "
                        + "permite filtrar por el estado del usuario, pero no la vigencia. Disponible para "
                        + "asesores de ficha y representantes del comité de currículo.";
        public static final String CONSULTAR_RESP_200 = "Página de asesores de ficha";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }

    public static final class Coordinador {

        private Coordinador() {}

        public static final String REMOVER_SUMMARY = "Remover información de un coordinador";
        public static final String REMOVER_DESCRIPTION =
                "Da de baja lógica el rol coordinador de un usuario: marca la fecha de eliminación sin borrar "
                        + "la fila, revoca el realm role coordinador en Keycloak y notifica a los contextos "
                        + "que replican al coordinador. El usuario y sus demás roles no cambian. "
                        + "Exclusivo del rol administrador.";
        public static final String REMOVER_RESP_204 = "Coordinador removido";
        public static final String REMOVER_RESP_400 = "Identificador de usuario inválido";
        public static final String REMOVER_RESP_422 = "El usuario no tiene un rol coordinador vigente";
        public static final String REMOVER_RESP_503 =
                "No fue posible revocar el rol en el proveedor de identidad; el servicio no está disponible temporalmente";

        public static final String CONSULTAR_ADMINISTRADOR_SUMMARY =
                "Consultar información de los coordinadores (administrador)";
        public static final String CONSULTAR_ADMINISTRADOR_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de todos los coordinadores, incluidos los dados de baja. "
                        + "Cada fila indica el estado del usuario y si el rol coordinador sigue vigente. "
                        + "Exclusivo del rol administrador.";
        public static final String CONSULTAR_VIGENTES_SUMMARY = "Consultar información de los coordinadores vigentes";
        public static final String CONSULTAR_VIGENTES_DESCRIPTION =
                "Lista paginada, filtrable y ordenable de los coordinadores con el rol vigente. No expone el "
                        + "estado del usuario ni la vigencia. Disponible para asesores, estudiantes y coordinadores.";
        public static final String CONSULTAR_RESP_200 = "Página de coordinadores";
        public static final String CONSULTAR_RESP_400 = "Filtro, orden o paginación inválidos";
    }
}
