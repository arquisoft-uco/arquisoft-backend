package com.arquisoft.shared.message;

public final class FichasMessages {

    private FichasMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // FichaPerfil
    // ─────────────────────────────────────────────────────────────────────────

    public static final class FichaPerfil {

        private FichaPerfil() {}

        // Campos
        public static final String CAMPO_ID              = "id";
        public static final String CAMPO_TITULO          = "tituloProyecto";
        public static final String CAMPO_ASESOR_FICHA_ID = "asesorFichaId";

        // Límites
        public static final int TITULO_MAX = 100;

        // Códigos de error
        public static final String ID_REQUERIDO            = "FICHA_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO        = "FICHA_TITULO_REQUERIDO";
        public static final String TITULO_DEMASIADO_LARGO  = "FICHA_TITULO_DEMASIADO_LARGO";
        public static final String ASESOR_REQUERIDO        = "FICHA_ASESOR_REQUERIDO";

        // Logs
        public static final String LOG_REGISTRADA            = "Ficha de perfil registrada — id={}";
        public static final String LOG_GUARDADA              = "FichaPerfil guardada: id={}";
        public static final String LOG_CONSULTANDO           = "Consultando fichas de perfil — pagina={}, tamanio={}";
        public static final String LOG_CONSULTA_COMPLETADA   = "Consulta fichas-perfil completada — total={}, pagina={}, tamanio={}";
        public static final String LOG_ORDENAMIENTO_INVALIDO  = "Campo de ordenamiento inválido: {}";
        public static final String LOG_USO_INVALIDO_API_ORDEN = "Uso inválido de la API de acceso a datos al ordenar: {}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AsesorFicha
    // ─────────────────────────────────────────────────────────────────────────

    public static final class AsesorFicha {

        private AsesorFicha() {}

        // Campos
        public static final String CAMPO_ID            = "id";
        public static final String CAMPO_IDENTIFICADOR = "identificador";
        public static final String CAMPO_NOMBRE        = "nombre";
        public static final String CAMPO_EMAIL         = "email";

        // (Sin códigos ni logs por ahora — el aggregate solo expone rebuild.)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Usuario (espejo de seguridad consumido vía AMQP)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Usuario {

        private Usuario() {}

        // Logs
        public static final String LOG_USUARIO_CREADO_RECIBIDO =
                "[FICHAS] UsuarioCreado recibido: usuarioId={} email={} rol={}";
        public static final String LOG_REGISTRADO_ESPEJO_SIMULADO =
                "[FICHAS] Usuario registrado en espejo (simulado): usuarioId={} email={} rol={}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MinIO (guías de fichas)
    // ─────────────────────────────────────────────────────────────────────────

    public static final class MinioGuia {

        private MinioGuia() {}

        // Logs
        public static final String LOG_UPLOAD_URL   = "GET /fichas/minio/guia/upload-url — bucket={}, key={}";
        public static final String LOG_DOWNLOAD_URL = "GET /fichas/minio/guia/download-url — bucket={}, key={}";
        public static final String LOG_DELETE       = "DELETE /fichas/minio/guia/objeto — bucket={}, key={}";
    }
}
