package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto biblioteca. Ver la nota de {@link FichasFields}.
 */
public final class BibliotecaFields {

    private BibliotecaFields() {}

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class Documentacion {

        private Documentacion() {}

        public static final String ROLES = "roles";
        public static final String DOCUMENTACION = "documentacion";
        public static final String ESTADO = "estado";
        public static final String ENTREGABLE_PROYECTO_GRADO = "entregableProyectoGrado";
        public static final String BIBLIOTECARIO = "bibliotecario";
        public static final String URL = "url";
    }

    public static final class Autor {

        private Autor() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class AutorDocumentacion {

        private AutorDocumentacion() {}

        public static final String DOCUMENTACION = "documentacion";
        public static final String AUTOR = "autor";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String PAGINA = "pagina";
        public static final String TAMANIO = "tamanio";
        public static final String ID = "id";
        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String VERSION_ENTREGABLE = "versionEntregable";
        public static final String ESTADO_ENTREGABLE = "estadoEntregable";
        public static final String CLAVE_OBJETO = "claveObjeto";
        public static final String NOMBRE_ARCHIVO = "nombreArchivo";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String ID = "id";
        public static final String TITULO = "titulo";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO = "proyectoGrado";
        public static final String AUTOR = "autor";
        public static final String AUTORES = "autores";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }
}
