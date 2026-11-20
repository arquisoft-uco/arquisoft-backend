package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto biblioteca. Ver la nota de {@link AppCodes}.
 */
public final class BibliotecaCodes {

    private BibliotecaCodes() {}

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String ID_REQUERIDO = "BIBLIOTECARIO_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "BIBLIOTECARIO_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "BIBLIOTECARIO_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "BIBLIOTECARIO_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "BIBLIOTECARIO_OCURRIDO_EN_REQUERIDO";
        public static final String NO_VIGENTE = "BIBLIOTECARIO_NO_VIGENTE";
    }

    public static final class EstadoDocumentacion {

        private EstadoDocumentacion() {}

        public static final String NO_ENCONTRADO = "ESTADO_DOCUMENTACION_NO_ENCONTRADO";
    }

    public static final class Documentacion {

        private Documentacion() {}

        public static final String ROLES_REQUERIDOS = "DOCUMENTACION_ROLES_REQUERIDOS";
        public static final String DOCUMENTACION_ID_REQUERIDO = "DOCUMENTACION_ID_REQUERIDO";
        public static final String NO_DESCARGABLE = "DOCUMENTACION_NO_DESCARGABLE";
        public static final String ARCHIVO_NO_DISPONIBLE = "DOCUMENTACION_ARCHIVO_NO_DISPONIBLE";
        public static final String ARCHIVO_AUSENTE = "DOCUMENTACION_ARCHIVO_AUSENTE";
        public static final String ESTADO_REQUERIDO = "DOCUMENTACION_ESTADO_REQUERIDO";
        public static final String ESTADO_INVALIDO = "DOCUMENTACION_ESTADO_INVALIDO";
        public static final String NO_ENCONTRADA = "DOCUMENTACION_NO_ENCONTRADA";
        public static final String ESTADO_DESTINO_NO_PERMITIDO = "DOCUMENTACION_ESTADO_DESTINO_NO_PERMITIDO";
        public static final String ESTADO_SIN_CAMBIO = "DOCUMENTACION_ESTADO_SIN_CAMBIO";
        public static final String ENTREGABLE_REQUERIDO = "DOCUMENTACION_ENTREGABLE_REQUERIDO";
        public static final String BIBLIOTECARIO_REQUERIDO = "DOCUMENTACION_BIBLIOTECARIO_REQUERIDO";
        public static final String URL_REQUERIDA = "DOCUMENTACION_URL_REQUERIDA";
        public static final String URL_LONGITUD_EXCEDIDA = "DOCUMENTACION_URL_LONGITUD_EXCEDIDA";
        public static final String YA_REGISTRADA = "DOCUMENTACION_YA_REGISTRADA";
        public static final String NO_COMPARTIBLE = "DOCUMENTACION_NO_COMPARTIBLE";
    }

    public static final class Autor {

        private Autor() {}

        public static final String ID_REQUERIDO = "AUTOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "AUTOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "AUTOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "AUTOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "AUTOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class AutorDocumentacion {

        private AutorDocumentacion() {}

        public static final String DOCUMENTACION_ID_REQUERIDO = "AUTOR_DOCUMENTACION_DOCUMENTACION_ID_REQUERIDO";
        public static final String AUTOR_REQUERIDO = "AUTOR_DOCUMENTACION_AUTOR_REQUERIDO";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String ID_REQUERIDO = "ENTREGABLE_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String NO_ENCONTRADO = "ENTREGABLE_PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String ARCHIVO_NO_DISPONIBLE = "ENTREGABLE_PROYECTO_GRADO_ARCHIVO_NO_DISPONIBLE";
        public static final String PAGINA_INVALIDA = "ENTREGABLE_PROYECTO_GRADO_PAGINA_INVALIDA";
        public static final String TAMANIO_INVALIDO = "ENTREGABLE_PROYECTO_GRADO_TAMANIO_INVALIDO";
        public static final String TITULO_LONGITUD_EXCEDIDA = "ENTREGABLE_PROYECTO_GRADO_TITULO_LONGITUD_EXCEDIDA";
        public static final String PROYECTO_GRADO_REQUERIDO = "ENTREGABLE_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String VERSION_INVALIDA = "ENTREGABLE_PROYECTO_GRADO_VERSION_INVALIDA";
        public static final String ESTADO_ENTREGABLE_REQUERIDO =
                "ENTREGABLE_PROYECTO_GRADO_ESTADO_ENTREGABLE_REQUERIDO";
        public static final String BUCKET_REQUERIDO = "ENTREGABLE_PROYECTO_GRADO_BUCKET_REQUERIDO";
        public static final String CLAVE_OBJETO_REQUERIDA = "ENTREGABLE_PROYECTO_GRADO_CLAVE_OBJETO_REQUERIDA";
        public static final String NOMBRE_ARCHIVO_REQUERIDO = "ENTREGABLE_PROYECTO_GRADO_NOMBRE_ARCHIVO_REQUERIDO";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID_REQUERIDO = "PROYECTO_GRADO_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO = "PROYECTO_GRADO_TITULO_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String SIN_AUTORES = "ESTUDIANTE_PROYECTO_GRADO_SIN_AUTORES";
        public static final String AUTOR_NO_VIGENTE = "ESTUDIANTE_PROYECTO_GRADO_AUTOR_NO_VIGENTE";
        public static final String PROYECTO_GRADO_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_PROYECTO_GRADO_REQUERIDO";
        public static final String AUTOR_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_AUTOR_REQUERIDO";
        public static final String AUTORES_REQUERIDOS = "ESTUDIANTE_PROYECTO_GRADO_AUTORES_REQUERIDOS";
        public static final String AUTOR_DUPLICADO = "ESTUDIANTE_PROYECTO_GRADO_AUTOR_DUPLICADO";
        public static final String OCURRIDO_EN_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_OCURRIDO_EN_REQUERIDO";
    }
}
