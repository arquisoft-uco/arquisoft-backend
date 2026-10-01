# shared:util

Helpers estáticos sin estado. No modelan nada del negocio: son las operaciones de bajo nivel que el
resto del código repetiría de otro modo.

| Clase | Cubre |
|---|---|
| `UtilText` | vacío/nulo, recorte, comparación |
| `UtilObject` | nulidad y valores por defecto |
| `UtilUUID` | conversión y validación de UUID |
| `UtilCollection` | colecciones vacías o nulas |
| `UtilDate` | fechas |
| `UtilNumber` | números |

## Por qué es un módulo propio

`DomainValidator` (`shared:validation`) usa `UtilText`, `UtilObject`, `UtilUUID` y
`UtilCollection`. Mientras estos helpers vivieron en `shared:domain`, y dado que `shared:domain`
expone `shared:validation` con `api`, la dependencia habría cerrado el ciclo
`validation → domain → validation`.

Como las seis clases son JDK puro —cero dependencias, ni siquiera entre ellas— sacarlas a una hoja
del grafo resuelve el ciclo sin coste. El paquete Java (`com.arquisoft.shared.util`) no cambió, así
que los 29 archivos que las importan siguen compilando sin editarse.

## Reglas

- **Sin dependencias.** Ni Spring, ni Jakarta, ni Lombok, ni otro módulo del proyecto. Es lo que
  permite que la capa de dominio lo importe sin violar «domain sin Spring ni Jakarta».
- **`shared:domain` lo expone con `api`**, para que los consumidores no tengan que declararlo.
- **Solo helpers sin estado.** Si algo necesita estado o conoce una regla de negocio, no va aquí.
