# shared:exception

Jerarquía base de excepciones del proyecto. Todo módulo que necesite lanzar un error del sistema
extiende una de estas clases; ninguna capa define su propia raíz.

## Qué contiene

| Clase | HTTP | Cuándo |
|---|---|---|
| `DomainException` | 422 | una regla de negocio del agregado se incumple (incluye "no es propietario") |
| `ApplicationException` | 400 | existencia, unicidad o integridad de la petición |
| `InfrastructureException` | 503 | un recurso externo no responde |
| `DomainValidationException` | 422 | varias reglas a la vez — lleva `fieldErrors[]` |
| `ApplicationValidationException` | 400 | varios errores de entrada a la vez |

`BaseException` es la raíz abstracta y `BaseError` el par mensaje + código que todas transportan.
`GlobalAppExceptionHandler` (`shared:web`) resuelve el status recorriendo la cadena de superclases,
así que una excepción nueva solo necesita extender la base correcta para responder con el código
adecuado.

`DomainValidationException` y `ApplicationValidationException` reciben un `ValidationResult` por
constructor. Ese tipo vive en `shared:validation`, que declara este módulo con `api` — la relación
es mutua (`ValidationResult` lanza esas dos excepciones), así que ambos lados se necesitan
visibles. Ver [shared/validation/README.md](../validation/README.md).

## Por qué es un módulo propio

`shared:domain` declara `api project(':shared:message')`. Cuando `shared:message` necesitó extender
`InfrastructureException` para el catálogo distribuido, la dependencia inversa habría cerrado un
ciclo que Gradle rechaza — y no hay scope que lo evite: `implementation`, `compileOnly` y
`compileOnlyApi` fallan igual, porque el ciclo es entre tareas de compilación.

Sacar estas clases a una hoja del grafo lo resuelve sin tocar ni una línea de código: el paquete
Java (`com.arquisoft.shared.exception`) no cambió, así que los archivos que las importan siguen
compilando sin editarse.

```
shared:exception   (hoja, sin dependencias)
    ↑ api                    ↑ implementation
shared:domain  ──api──→  shared:message
```

## Reglas

- **Sin dependencias.** Ni Spring, ni Jakarta, ni Lombok, ni otro módulo del proyecto. Es lo que
  permite que la capa de dominio lo importe sin violar «domain sin Spring ni Jakarta».
- **`shared:domain` lo expone con `api`, nunca con `implementation`.** Los paquetes
  `com.arquisoft.shared.exception` y `com.arquisoft.shared.validation` los importan ~100 archivos
  que solo declaran `shared:domain`; con `implementation` todos dejarían de compilar.
- **No crece con lógica de negocio.** Aquí solo van los tipos base. Una excepción concreta
  (`AsesorFichaNoEncontradoException`) vive en el contexto que la lanza.
