# POO-08 — auditoría y evidencia real

Fecha: 10/11/2026 (America/Argentina/Buenos_Aires). Proyecto válido: raíz y `src/`.

## Auditoría inicial, antes de modificar código

Los estados de esta primera tabla corresponden a la inspección inicial, no al resultado final. La existencia de clases/tests no se tomó como evidencia de ejecución.

| Requisito | Estado | Evidencia | Falta |
| --- | --- | --- | --- |
| Auditoría raíz | ✅ completo | pom, propiedades, dominio, repository, services, controllers, DTO, tests, README y bitácora leídos | Registrar esta comparación |
| Java 21 / arranque | ⚠️ incompleto | POM Java 21 y wrapper; java ausente del PATH | Preparar JDK y ejecutar |
| JPA/H2 | ✅ completo | starter-data-jpa, h2; archivo ./data/entidades; sa; clave vacía; update; consola habilitada | Prueba real pendiente |
| Archivos H2 ignorados | ✅ completo | data/ ignorado; ningún archivo H2 trackeado | Nada |
| Tres clases persistidas | ✅ completo | Creeper, Jugador y Cerdo con @Entity; UUID heredado; SINGLE_TABLE; constructores protected | Ejecutar tests JPA |
| CRUD | ⚠️ incompleto | CRUD implementado contra repository JPA y tests existentes | Ejecución real |
| POO-06 | ✅ completo | Abstracción, sobreescritura, sobrecarga y constructores conservados | Comprobar regresión |
| Interface POO-07 | ⚠️ incompleto | UICapable en domain; Zombie y Cerdo; /api/sonidos usa el tipo de la interface | Test del endpoint |
| Dos reglas de explosión | ⚠️ incompleto | Daño por distancia y consumo del creeper implementados | Mover cálculo y rechazo del creeper muerto al dominio |
| Excepciones | ⚠️ incompleto | EntidadNoEncontradaException y advice existentes | Base común, hija de explosión y códigos distinguibles |
| POST ilegal | ⚠️ incompleto | Validaciones en dominio/service y tests existentes | HTTP y GET posterior |
| Insomnia | ❌ falta | No había colección | Colección importable y orden |
| README | ⚠️ incompleto | CRUD/JPA/POO-06 ya documentados | Completar POO-07/08, Mermaid y 22 respuestas |
| Bitácora | ⚠️ incompleto | Etapa POO-06 presente | Agregar POO-08 sin borrar historial |
| Tests/HTTP | ⚠️ incompleto | Cinco clases de tests existentes | Ejecutar y completar cobertura faltante |
| Consola H2 | ⚠️ incompleto | Configurada | Conexión y SELECT real |
| bin/ y Git | ✅ completo | git ls-files bin y status inicial sin salida; bin/ ignorado y ausente | No borrar ni versionar |

## Cambios mínimos realizados

Se conservaron POM, propiedades, repository, DTO y controller CRUD. Se mantuvo la URL existente de simulación `/api/explosiones`. El cálculo de daño radial y la prohibición de detonar un creeper muerto pasaron al dominio; el service conserva selección, bloqueos y transacción. Se agregó la base de excepciones y la hija de explosión, reutilizando la clase de recurso inexistente. Se completaron cobertura faltante, Insomnia, README y bitácora.

Las tres clases elegidas son **Creeper, Jugador y Cerdo**, todas `@Entity`, con raíz `Entidad` (`SINGLE_TABLE`, `@Id UUID`). Las otras cinco clases concretas persistidas se conservaron. Los constructores vacíos son `protected` y no se agregaron setters generales.

## Java y tests ejecutados

JDK usado: Eclipse Temurin **21.0.13+9-LTS**. No había JDK 21 completo en PATH: se descargó temporalmente en `/tmp/poo08-jdk21`. Maven y sus dependencias se aislaron en `/tmp/poo08-maven`, sin cambios en el POM ni en el wrapper.

En este Linux se ejecutó el equivalente de `.\mvnw clean test`:

```sh
JAVA_HOME=/tmp/poo08-jdk21 MAVEN_USER_HOME=/tmp/poo08-maven ./mvnw -Dmaven.repo.local=/tmp/poo08-maven/repository clean test
```

```text
InvariantesDominioTests:   Tests run: 5,  Failures: 0, Errors: 0, Skipped: 0
ApplicationTests:          Tests run: 6,  Failures: 0, Errors: 0, Skipped: 0
ExplosionServiceRestTests: Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
EntidadJpaTests:           Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
EntidadCrudTests:          Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
EntidadMigrationTests:     Tests run: 1,  Failures: 0, Errors: 0, Skipped: 0

Tests run: 70
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Cobertura verificada: carga del contexto Spring, regresión POO-06, persistencia de todas las subclases tras limpiar el contexto JPA, inventario/relaciones/migración, CRUD, POST inválido, colección/id, actualización y GET posterior, DELETE y GET 404, interface POO-07, ambas reglas del dominio, simulación, GET posteriores, ambas excepciones, rollback y concurrencia. Se ampliaron los tests existentes; no se agregaron casos para inflar la cantidad.

## HTTP histórico contra H2 en archivo — colección anterior

La aplicación se levantó desde la raíz con:

```sh
JAVA_HOME=/tmp/poo08-jdk21 MAVEN_USER_HOME=/tmp/poo08-maven ./mvnw -Dmaven.repo.local=/tmp/poo08-maven/repository spring-boot:run
```

Con JDK 21 y Maven configurados en el entorno, el comando normal sigue siendo `./mvnw spring-boot:run`.

Esta tabla conserva la ejecución inicial de la colección anterior de 22 requests. El flujo vigente de 16 etapas está en [insomnia/VERIFICACION-FLUJO.md](insomnia/VERIFICACION-FLUJO.md).

Base de todas las rutas de la tabla: **http://localhost:8080**. Los alias siguientes corresponden a UUID devueltos realmente por POST en esta ejecución; no son IDs para reutilizar en Insomnia.

| Alias en la tabla | UUID real |
| --- | --- |
| `crud_id` | `80dd95c3-dea5-4389-8667-26f63346ef40` |
| `creeper_id` | `ff8f0c68-a8b4-4196-b3c9-306259e9387f` |
| `jugador_id` | `dfdb7713-99d7-4ce3-a920-700b757ae82b` |
| `cerdo_id` | `19560b16-fbc1-44c1-a8f0-3624e68170a2` |

| Paso | Método y URL (relativa a la base) | HTTP | JSON/cuerpo resumido |
| --- | --- | --- | --- |
| 01 | `GET /` | 200 | `"API REST - Sofia Torres - Minecraft"` |
| 02 | `POST /api/entidades` | 201 | `{"nombre":"ZombieInsomnia","tipo":"ZOMBIE","salud":20.0,"velocidad":3.0}` |
| 03 | `GET /api/entidades` | 200 | `{"cantidad":1,"nombres":["ZombieInsomnia"]}` |
| 04 | `GET /api/entidades/{crud_id}` | 200 | `{"nombre":"ZombieInsomnia","tipo":"ZOMBIE","salud":20.0,"velocidad":3.0}` |
| 05 | `PUT /api/entidades/{crud_id}` | 200 | `{"nombre":"ZombieActualizado","tipo":"ZOMBIE","salud":15.0,"velocidad":5.0}` |
| 06 | `GET /api/entidades/{crud_id}` | 200 | `{"nombre":"ZombieActualizado","tipo":"ZOMBIE","salud":15.0,"velocidad":5.0}` |
| 07 | `POST /api/entidades` | 400 | `{"codigo":"DATOS_INVALIDOS","error":"La velocidad debe ser finita y no negativa"}` |
| 08 | `GET /api/entidades` | 200 | `{"cantidad":1,"nombres":["ZombieActualizado"]}` |
| 09 | `GET /api/sonidos` | 200 | `{"resultados":[{"tipo":"Zombie","mensaje":"Grrr..."},{"tipo":"Cerdo","mensaje":"Oink, oink!"}]}` |
| 10 | `POST /api/entidades` | 201 | `{"nombre":"CreeperInsomnia","tipo":"CREEPER","salud":20.0}` |
| 11 | `POST /api/entidades` | 201 | `{"nombre":"JugadorInsomnia","tipo":"JUGADOR","salud":20.0}` |
| 12 | `POST /api/entidades` | 201 | `{"nombre":"CerdoInsomnia","tipo":"CERDO","salud":10.0}` |
| 13 | `POST /api/explosiones` | 200 | `{"saludCreeper":0.0,"resultados":[{"tipo":"JUGADOR","danioAplicado":15.0,"saludActual":5.0},{"tipo":"CERDO","danioAplicado":10.0,"saludActual":0.0}]}` |
| 14 | `GET /api/entidades/{creeper_id}` | 200 | `{"nombre":"CreeperInsomnia","tipo":"CREEPER","salud":0.0,"fuseTime":0,"encendido":false}` |
| 15 | `GET /api/entidades/{jugador_id}` | 200 | `{"nombre":"JugadorInsomnia","tipo":"JUGADOR","salud":5.0}` |
| 16 | `GET /api/entidades/{cerdo_id}` | 200 | `{"nombre":"CerdoInsomnia","tipo":"CERDO","salud":0.0}` |
| 17 | `POST /api/explosiones` | 409 | `{"codigo":"EXPLOSION_INVALIDA","error":"Un creeper muerto no puede explotar"}` |
| 18 | `GET /api/entidades/{jugador_id}` | 200 | `{"nombre":"JugadorInsomnia","tipo":"JUGADOR","salud":5.0}` |
| 19 | `DELETE /api/entidades/{crud_id}` | 204 | `"(sin cuerpo)"` |
| 20 | `GET /api/entidades/{crud_id}` | 404 | `{"codigo":"ENTIDAD_NO_ENCONTRADA","error":"No existe una entidad con id 80dd95c3-dea5-4389-8667-26f63346ef40"}` |
| 21 | `POST /api/explosiones` | 404 | `{"codigo":"ENTIDAD_NO_ENCONTRADA","error":"No existe una entidad con id 80dd95c3-dea5-4389-8667-26f63346ef40"}` |
| 22 | `GET /api/entidades` | 200 | `{"cantidad":3,"nombres":["CreeperInsomnia","JugadorInsomnia","CerdoInsomnia"]}` |

Los POST válidos devolvieron `Location: /api/entidades/{id}`. Antes/después del POST inválido la colección tenía **1** registro; al finalizar quedaron **3**, los participantes de la simulación. El Zombie inválido nunca apareció. Repetir la explosión devolvió 409 y el Jugador siguió con salud 5.

## Consola H2 y filas persistidas

- Consola: http://localhost:8080/h2-console/ → **HTTP 200**; se obtuvo el formulario y se conectó mediante la sesión web de H2.
- Driver: `org.h2.Driver`.
- JDBC URL: `jdbc:h2:file:./data/entidades`.
- Usuario: `sa`. Contraseña: vacía.
- `ddl-auto=update`; consola habilitada. Tabla principal: `ENTIDADES`.

Se ejecutó exclusivamente esta consulta de lectura desde la consola, filtrada por los UUID originales:

```sql
SELECT DTYPE, ID, NOMBRE, SALUD, FUSE_TIME, ENCENDIDO
FROM ENTIDADES
WHERE ID IN (
  'ff8f0c68-a8b4-4196-b3c9-306259e9387f',
  'dfdb7713-99d7-4ce3-a920-700b757ae82b',
  '19560b16-fbc1-44c1-a8f0-3624e68170a2'
) ORDER BY NOMBRE;
```

| DTYPE | NOMBRE | SALUD | FUSE_TIME | ENCENDIDO |
| --- | --- | --- | --- | --- |
| Cerdo | CerdoInsomnia | 0.0 | NULL | NULL |
| Creeper | CreeperInsomnia | 0.0 | 0 | FALSE |
| Jugador | JugadorInsomnia | 5.0 | NULL | NULL |

Resultado de la consola: **3 rows**. No se insertaron ni modificaron datos manualmente en H2.

Después se detuvo y reinició **la misma aplicación** desde la raíz. Con los mismos UUID, los tres GET devolvieron 200 y salud **0, 5, 0**; el Creeper conservó `fuseTime=0` y `encendido=false`. El GET del Zombie eliminado continuó en 404. Esto comprueba persistencia en archivo, además del GET posterior dentro de la misma ejecución.

Al finalizar se detuvo el servidor de prueba. Las tres filas siguen en `data/`
para revisión; al arrancar de nuevo desde la raíz estarán disponibles.

## Colección Insomnia vigente

Archivo: [insomnia/minecraft-poo08.json](insomnia/minecraft-poo08.json). JSON v4,
16 carpetas ordenadas y 25 requests encadenadas. Contiene Base Environment con
`base_url=http://localhost:8080` y Environment **Local**, donde los POST guardan
los UUID automáticamente. `entidad_id` y `objetivo_id` identifican al mismo
Jugador; `creeper_id` y `cerdo_id` provienen de los otros POST. El auxiliar de
12 genera el `inexistente_id` real usado en los casos 404.

El importador oficial aceptó los 44 recursos y conservó los scripts. Se
verificaron 25 peticiones HTTP reales y 43 comprobaciones de los scripts
exportados mediante un comprobador local de la API de scripting. La prueba no
consiste en una ejecución mediante la interfaz de Insomnia. El README documenta
compatibilidad, selección de Local, captura automática y alternativa manual.
Los resultados completos y UUID de esta ejecución están en
[VERIFICACION-FLUJO.md](insomnia/VERIFICACION-FLUJO.md).

## Revisión adicional según la rúbrica CYT646

La rúbrica fue aportada después de la primera verificación de POO-08. La
comprobación técnica original no acredita por sí sola la entrega formal ni una
nota de 24/24. El commit que se califica todavía debe crearse y publicarse tras
la revisión del usuario; no corresponde enlazar el HEAD anterior.

| Criterio de la rúbrica | Estado verificable | Evidencia / pendiente |
| --- | --- | --- |
| 1. Entrega y arranque | ⚠️ pendiente de entrega | Arranque Java 21 probado y licencia Apache 2.0 presente; faltan commit final accesible, comprobar acceso público y puntualidad en Classroom |
| 2. Paquetes | ✅ código verificado | Paquetes y carpetas coinciden; dominio separado de HTTP |
| 3. Ocultamiento e invariantes | ✅ código verificado | Campos privados, métodos de negocio y cinco nuevos casos borde probados directamente en dominio |
| 4. Herencia y sobreescritura | ✅ código verificado | Entidad abstracta, hijas, uso del padre y jerarquía de excepciones con handlers por tipo |
| 5. Constructores y sobrecarga | ✅ código verificado | Constructores de Zombie y firmas de mover conservados; validaciones en el dominio |
| 6. Comportamiento observable | ✅ evidencia técnica preparada | CRUD, H2, interface, simulación y excepciones probados; colección lista para demostrar en Insomnia |
| 7. Git | ⚠️ pendiente de versionar | Cambios sin staging; separar commits por aportes estables y enlazar el commit final después de revisión |
| 8. Documentación y defensa | ⚠️ pendiente de completar entrega | README/Mermaid, bitácora con modelos informados y especificaciones preparados; faltan entrega en Classroom y defensa |

### Casos borde reproducidos y corregidos

Antes de corregir, `InvariantesDominioTests` ejecutó cinco casos: cuatro fallos
y un error. Demostraron que una llamada directa al dominio permitía nombre de
101 caracteres, edad negativa por overflow, experiencia negativa por overflow,
reencendido del creeper consumido y una excepción durante tick al morir por
veneno justo antes de detonar.

Las correcciones validan nombre en el dominio, mantienen edad en el máximo de
int, rechazan la suma de experiencia antes de modificarla y protegen el ciclo de
encendido/cuenta regresiva del creeper. Los mismos cinco tests pasan después.
La suite completa final suma **70 tests, 0 fallos, 0 errores y 0 omitidos**.

También se arrancó la versión corregida con el mismo Maven Wrapper y H2 en
archivo. Se repitieron estas consultas HTTP sin crear datos nuevos:

| Consulta | HTTP | Resultado |
| --- | --- | --- |
| GET `http://localhost:8080/` | 200 | Presentación original |
| GET `http://localhost:8080/api/sonidos` | 200 | Dos sonidos, mediante UICapable |
| GET de los UUID originales de Creeper/Jugador/Cerdo | 200 cada uno | Salud 0 / 5 / 0; mecha consumida del Creeper conservada |
| GET del UUID original del Zombie eliminado | 404 | ENTIDAD_NO_ENCONTRADA |
| GET `http://localhost:8080/api/zombie?nombre=<101 caracteres>&x=0&y=64&z=0&velocidad=1.5` | 400 | El nombre no puede superar los 100 caracteres |

El último endpoint construye el Zombie directamente, sin EntidadService, por
lo que el 400 verifica que la nueva protección reside en el dominio. Se detuvo
el servidor de prueba al terminar, conservando las tres filas anteriores.

La ejecución dentro del sandbox bloqueó la conexión del agente de Mockito a la
JVM. Se repitió la suite completa fuera del sandbox, con las dependencias ya
locales y sin cambiar el POM; ese es el resultado exitoso consignado arriba.

### Material de entrega y modelos de IA

Se preparó [ESPECIFICACIONES-POO-08.md](ESPECIFICACIONES-POO-08.md) con objetivo,
consignas, contrato HTTP, forma de probar el Minecraft y recorrido de defensa.
No se subió a Classroom ni se inventó una fecha límite.

El usuario confirmó exactamente `gpt 6.1 sol` para **ChatGPT y Codex**, y así se
registró para esta etapa en la bitácora. Se conservó el registro histórico de
POO-06. El último modelo informado para **GitHub Copilot** es
`GPT-6.1 Sol High`, registrado en el cierre formal de la bitácora. El dato
anterior `fusion standard` se conserva allí como historial. Los nombres se
consignan según lo informado por el usuario, sin verificación independiente
de las interfaces de esas herramientas.

## Verificación técnica de POO-08

| Requisito | Estado | Evidencia |
| --- | --- | --- |
| 1. Auditoría previa | ✅ COMPLETO | Lectura real y tabla inicial de este informe |
| 2. H2/JPA | ✅ COMPLETO | Configuración existente conservada; archivo H2 ignorado; tests y conexión real |
| 3. Tres clases | ✅ COMPLETO | Creeper/Jugador/Cerdo; SINGLE_TABLE; UUID; protected; invariantes y constructores intactos |
| 4. CRUD REST | ✅ COMPLETO | HTTP 201/200/204/404 y GET posteriores; repository JPA |
| 5. Interface POO-07 | ✅ COMPLETO | UICapable; Zombie/Cerdo; GET /api/sonidos probado; flujo sin selección por clase |
| 6. Dos reglas | ✅ COMPLETO | Entidad.recibirExplosion y Creeper.finalizarExplosion/validarPuedeExplotar; transacción en ExplosionService |
| 7. Excepciones | ✅ COMPLETO | EntidadException → EntidadNoEncontradaException (404) / ExplosionInvalidaException (409); handlers por tipo |
| 8. POST ilegal | ✅ COMPLETO | 400 por velocidad -1; colección sin registro inválido ni aumento de cantidad |
| 9. Insomnia | ✅ COMPLETO | JSON v4; Environment Local; 16 etapas y 25 requests; importador oficial acepta 44 recursos |
| 10. README | ✅ COMPLETO | Arranque, H2, clases, interface, reglas, excepciones, Mermaid, 22 respuestas y orden Insomnia |
| 11. BITACORA | ✅ COMPLETO | Etapa POO-08, prompts resumidos y modelos de ChatGPT/Codex/Copilot registrados según lo informado por el usuario |
| 12. Tests | ✅ COMPLETO | 70 ejecutados, 0 fallos, 0 errores, 0 omitidos; BUILD SUCCESS con Java 21 |
| 13. HTTP real | ✅ COMPLETO | Flujo vigente: 25 requests HTTP y 43 comprobaciones de scripts; evidencia inicial y de reinicio conservada |
| 14. Consola H2 | ✅ COMPLETO | HTTP 200, conexión a JDBC configurada, SELECT de 3 filas; sin DML manual |
| 15. bin/ | ✅ COMPLETO | No trackeado, ignorado y ausente; no se creó ni eliminó |
| 16. Verificación final | ✅ COMPLETO | Tabla, árbol, URLs, resultados y estado Git en este informe |
| 17. Git | ✅ COMPLETO | Sin add/commit/push; recomendaciones de separación en README |

## Árbol final

```text
src/main/java/py/edu/uc/lp3/
├── domain/
│   ├── Aldeano.java
│   ├── Animal.java
│   ├── Cerdo.java
│   ├── Creeper.java
│   ├── Enderman.java
│   ├── Entidad.java
│   ├── EntidadException.java
│   ├── Esqueleto.java
│   ├── ExplosionInvalidaException.java
│   ├── Inventario.java
│   ├── Item.java
│   ├── Jugador.java
│   ├── MobHostil.java
│   ├── OtraEntidad.java
│   ├── Residente.java
│   ├── SerVivo.java
│   ├── TipoEntidad.java
│   ├── UICapable.java
│   ├── Vector3D.java
│   └── Zombie.java
├── dto/
│   ├── EntidadRequest.java
│   ├── EntidadResponse.java
│   ├── ExplosionRequest.java
│   └── ExplosionResponse.java
├── persistence/
│   └── repository/
│       └── EntidadRepository.java
├── rest/
│   └── controller/
│       ├── ComportamientoController.java
│       ├── EntidadController.java
│       ├── EntidadExceptionHandler.java
│       ├── ExplosionController.java
│       ├── IndexController.java
│       ├── SonidoController.java
│       └── ZombieController.java
├── service/
│   ├── EntidadNoEncontradaException.java
│   ├── EntidadService.java
│   └── ExplosionService.java
└── Application.java
```

## URLs finales

| Método | URL |
| --- | --- |
| GET | `http://localhost:8080/` |
| GET / POST | `http://localhost:8080/api/entidades` |
| GET / PUT / DELETE | `http://localhost:8080/api/entidades/{uuid}` |
| GET | `http://localhost:8080/api/sonidos` |
| POST | `http://localhost:8080/api/explosiones` |
| GET | `http://localhost:8080/api/comportamientos` |
| GET | `http://localhost:8080/api/zombie` |
| GET | `http://localhost:8080/h2-console/` |

Clases elegidas: Creeper/Jugador/Cerdo. Herencia: SINGLE_TABLE. Interface: UICapable. Reglas: daño radial y consumo/rechazo de repetición del creeper muerto. Excepciones: EntidadException → EntidadNoEncontradaException / ExplosionInvalidaException.

## Git y bin/

`git ls-files bin` no devolvió ninguna línea. `git check-ignore bin/ data/` confirmó ambas exclusiones. `bin/` no existe en esta copia; si se genera localmente, mantenerlo ignorado y usar la raíz. No se eliminó nada. La H2 de prueba HTTP quedó en `data/`, ignorada, con las tres filas de evidencia.

No se ejecutó `git add`, `git commit` ni `git push`. El índice permanece sin cambios. `git diff --check` no informó errores. Los commits sugeridos están en README, sin fabricar cambios en dependencias/repository/controller ya completos.

Estado final (`git status --short`):

```text
 M BITACORA.md
 M README.md
 M src/main/java/py/edu/uc/lp3/domain/Animal.java
 M src/main/java/py/edu/uc/lp3/domain/Creeper.java
 M src/main/java/py/edu/uc/lp3/domain/Entidad.java
 M src/main/java/py/edu/uc/lp3/domain/Jugador.java
 M src/main/java/py/edu/uc/lp3/domain/Vector3D.java
 M src/main/java/py/edu/uc/lp3/rest/controller/EntidadExceptionHandler.java
 M src/main/java/py/edu/uc/lp3/service/EntidadNoEncontradaException.java
 M src/main/java/py/edu/uc/lp3/service/ExplosionService.java
 M src/test/java/py/edu/uc/lp3/ApplicationTests.java
 M src/test/java/py/edu/uc/lp3/EntidadCrudTests.java
 M src/test/java/py/edu/uc/lp3/ExplosionServiceRestTests.java
?? docs/
?? src/main/java/py/edu/uc/lp3/domain/EntidadException.java
?? src/main/java/py/edu/uc/lp3/domain/ExplosionInvalidaException.java
?? src/test/java/py/edu/uc/lp3/InvariantesDominioTests.java
```

Cambios en archivos ya trackeados (`git diff --stat`; no incluye los nuevos `??`):

```text
 BITACORA.md                                        | 102 +++++++
 README.md                                          | 304 ++++++++++++++++++++-
 src/main/java/py/edu/uc/lp3/domain/Animal.java     |   4 +-
 src/main/java/py/edu/uc/lp3/domain/Creeper.java    |  18 ++
 src/main/java/py/edu/uc/lp3/domain/Entidad.java    |  18 +-
 src/main/java/py/edu/uc/lp3/domain/Jugador.java    |   3 +
 src/main/java/py/edu/uc/lp3/domain/Vector3D.java   |   5 +
 .../rest/controller/EntidadExceptionHandler.java   |  18 +-
 .../lp3/service/EntidadNoEncontradaException.java  |   4 +-
 .../py/edu/uc/lp3/service/ExplosionService.java    |  22 +-
 src/test/java/py/edu/uc/lp3/ApplicationTests.java  |  11 +
 src/test/java/py/edu/uc/lp3/EntidadCrudTests.java  |   9 +-
 .../py/edu/uc/lp3/ExplosionServiceRestTests.java   |  48 +++-
 13 files changed, 531 insertions(+), 35 deletions(-)
```

Archivos nuevos: este informe, `docs/insomnia/VERIFICACION-FLUJO.md`, `docs/ESPECIFICACIONES-POO-08.md`, la colección Insomnia, `domain/EntidadException.java`, `domain/ExplosionInvalidaException.java` y `src/test/java/py/edu/uc/lp3/InvariantesDominioTests.java`.
