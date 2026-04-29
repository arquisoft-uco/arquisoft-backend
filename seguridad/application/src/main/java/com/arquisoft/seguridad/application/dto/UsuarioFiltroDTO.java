package com.arquisoft.seguridad.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * DTO de entrada que encapsula los parámetros opcionales de filtro y paginación
 * para la consulta de usuarios.
 *
 * <p>Modelado como {@code record} de Java 21: inmutable, solo transporta datos.
 * Las anotaciones Jakarta Validation se aplican sobre los componentes del record.
 *
 * <p>Todos los filtros son opcionales. Si no se envía ninguno, se retornan todos
 * los usuarios paginados con los valores por defecto.
 *
 * @param nombreOEmail filtro parcial (LIKE, case-insensitive) sobre nombre, apellido o email;
 *                     {@code null} si no se aplica
 * @param estado       valor string del enum {@code EstadoUsuario} ({@code "ACTIVO"} o
 *                     {@code "INACTIVO"}); {@code null} si no se aplica. La conversión al
 *                     enum se realiza en el use case (POL-04).
 * @param rol          valor string del enum {@code UsuarioRole} (ej. {@code "ESTUDIANTE"});
 *                     {@code null} si no se aplica. La conversión se realiza en el use case.
 * @param pagina       número de página (0-indexed); mínimo 0
 * @param tamano       tamaño de página; mínimo 1, máximo 100
 */
public record UsuarioFiltroDTO(
        String nombreOEmail,
        String estado,
        String rol,
        @Min(0) int pagina,
        @Min(1) @Max(100) int tamano
) {

    /**
     * Constructor compacto con valores por defecto aplicados cuando {@code pagina} o
     * {@code tamano} no son provistos explícitamente por el caller.
     * La validación de rango la ejecuta el framework de validación Jakarta.
     */
    public UsuarioFiltroDTO {
        // Los valores por defecto se aplican en el Controller vía @RequestParam(defaultValue)
        // Este constructor compacto no requiere lógica adicional
    }

    /**
     * Factory method de conveniencia para construir el DTO con valores por defecto
     * de paginación ({@code pagina=0}, {@code tamano=20}).
     */
    public static UsuarioFiltroDTO conDefectos(String nombreOEmail, String estado, String rol) {
        return new UsuarioFiltroDTO(nombreOEmail, estado, rol, 0, 20);
    }
}
