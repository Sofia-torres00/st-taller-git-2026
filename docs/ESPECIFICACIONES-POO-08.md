# Especificaciones POO-08 — Minecraft

Alumna: Sofia Torres. Materia: Lenguaje de Programación 3 (CYT646).
Ejercicio: `ejercicio-poo-08`. Dominio: Minecraft.

Este documento describe la solución del proyecto para acompañar la entrega en
Classroom. Se basa en los requisitos de POO-08 y la rúbrica aportados por el
usuario; no sustituye el enunciado oficial del docente.

## Objetivo

Persistir los personajes del mundo, administrar sus registros por una API REST
y simular una explosión entre un Creeper, un Jugador y un Cerdo. Conservar el
polimorfismo, la sobrecarga y la interface de sonido de las etapas anteriores.

## Repositorio y entrega

- Repositorio configurado: https://github.com/Sofia-torres00/st-taller-git-2026
- Commit de la solución: **pendiente de crear y publicar tras revisión**.
- Enlace a completar con el hash completo:
  `https://github.com/Sofia-torres00/st-taller-git-2026/commit/<HASH_COMPLETO_DE_LA_SOLUCION>`.
- Fecha límite de Classroom: **pendiente de consultar en Classroom**.
- Subir este Markdown y el enlace exacto al commit en la entrega de Classroom.
- Comprobar que el repositorio y el commit sean accesibles para el docente.

El HEAD previo a esta revisión no contiene los cambios todavía sin versionar:
no usar su enlace como si fuera el commit de la solución actual.

## Consignas aplicadas al proyecto

1. Usar el proyecto raíz y `src/`, Java 21 y Maven Wrapper; excluir `bin/`,
   `target/` y los archivos H2 generados del versionado.
2. Mantener `domain`, `dto`, `persistence/repository`, `service` y
   `rest/controller`, con paquetes que coincidan con las carpetas.
3. Persistir Creeper, Jugador y Cerdo dentro de la jerarquía JPA de `Entidad`.
   Conservar las demás subclases existentes. Usar `SINGLE_TABLE`, discriminador
   `DTYPE` y UUID generado por el dominio en el servidor.
4. Proteger el estado con campos privados, constructores de negocio válidos y
   constructores vacíos `protected` para JPA. Mantener los métodos abstractos y
   sobreescritos, los constructores sobrecargados de Zombie y las dos firmas de
   `Entidad.mover`.
5. Completar el CRUD de `/api/entidades` mediante repository JPA. El JSON de
   creación no requiere id. La respuesta incluye UUID, `tipo` y `atributos`.
6. Reutilizar `UICapable.emitirSonido()` en `/api/sonidos`, con Zombie y Cerdo
   tratados como esa interface.
7. Coordinar la simulación desde `ExplosionService`: cargar y bloquear tres
   participantes existentes, distintos y de los roles solicitados, ejecutar
   reglas del dominio y confirmar todos los cambios en una transacción.
8. Traducir las excepciones mediante handlers específicos y devolver códigos
   distinguibles; conservar las reglas fuera del controller.
9. Entregar una colección importable de Insomnia, README y bitácora de IA.
10. Separar los commits por funcionalidades o partes estables después de que
    el usuario revise los cambios. Esta preparación no ejecuta comandos Git de
    escritura ni realiza la entrega en Classroom.

## Contrato HTTP

Base: `http://localhost:8080`.

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | `/` | 200: presentación de la API |
| POST | `/api/entidades` | 201: registro creado, UUID y Location |
| GET | `/api/entidades` | 200: registros consultados en H2 |
| GET | `/api/entidades/{id}` | 200 o 404 |
| PUT | `/api/entidades/{id}` | 200 o 400/404; cambio persistido |
| DELETE | `/api/entidades/{id}` | 204; GET posterior 404 |
| GET | `/api/sonidos` | 200: comportamiento mediante UICapable |
| GET | `/api/comportamientos` | 200: sobreescritura mediante Entidad |
| POST | `/api/explosiones` | 200 o 400/404/409 |

PUT requiere nombre, tipo, salud y coordenadas completas. Los atributos
específicos omitidos conservan su valor. No permite cambiar el subtipo ni la
capacidad del inventario existente. DELETE elimina físicamente y desvincula los
mobs que tenían a ese personaje como objetivo.

## Reglas e invariantes

- Daño radial: un participante vivo a menos de seis bloques recibe
  `20 × (1 − distancia/6)`; la salud mínima es cero. A seis bloques o más no
  recibe daño. La regla vive en `Entidad.recibirExplosion`.
- Consumo: `Creeper.finalizarExplosion` deja salud y mecha en cero y apaga al
  creeper. Uno muerto no detona ni se enciende. Para encenderse necesita además
  una mecha positiva. Morir por veneno detiene la cuenta regresiva.
- El dominio exige salud y velocidad finitas y no negativas, coordenadas
  finitas, edad no negativa, nombre de hasta 100 caracteres tras quitar espacios
  de los extremos y tiempos positivos al crear o configurar una mecha.
- En el límite de `int`, la edad permanece en `Integer.MAX_VALUE`; una suma de
  experiencia que desbordaría ese límite se rechaza sin modificar la experiencia.

| Excepción | Base | HTTP | Código JSON |
| --- | --- | --- | --- |
| EntidadNoEncontradaException | EntidadException | 404 | ENTIDAD_NO_ENCONTRADA |
| ExplosionInvalidaException | EntidadException | 409 | EXPLOSION_INVALIDA |

`EntidadException` deriva de `RuntimeException`: los fallos interrumpen la
operación y provocan rollback transaccional. El JSON incluye `codigo` y `error`.

## Forma de probarlo

Desde la raíz, con JDK 21 seleccionado:

```sh
./mvnw clean test
./mvnw spring-boot:run
```

Importar [insomnia/minecraft-poo08.json](insomnia/minecraft-poo08.json) en Insomnia.
El Environment contiene `base_url=http://localhost:8080`. Ejecutar las requests
de las 16 carpetas en orden, incluyendo sus subrequests (25 en total), con el
Environment **Local** seleccionado. Los POST capturan automáticamente los UUID
en `entidad_id`/`objetivo_id`, `creeper_id`, `cerdo_id` e `inexistente_id`. Si la
versión no ejecuta scripts, copiar el id devuelto por el POST y pegarlo en la
variable correspondiente. El orden completo está en el
[README](../README.md#importar-y-ejecutar-insomnia).

Comprobar especialmente:

1. CRUD: POST 201, GET, PUT, GET con cambios, DELETE 204 y GET 404.
2. POST Zombie con `atributos.velocidad=-1`: 400; GET colección sin registro
   inválido ni incremento de la cantidad.
3. Interface: GET `/api/sonidos` devuelve los sonidos de Zombie y Cerdo.
4. Explosión: Creeper en X=1000, Jugador en X=1001.5 y Cerdo en X=1003,
   todos con Y=64 y Z=0, con saludes iniciales 20, 20 y 10.
5. POST `/api/explosiones` con sus tres UUID: 200; GET posteriores con saludes
   0, 5 y 0. Creeper con `fuseTime=0` y `encendido=false`.
6. Repetir la explosión: 409, `EXPLOSION_INVALIDA`, sin daño adicional.
7. En 12 crear y eliminar un auxiliar; su UUID real permite comprobar GET 404
   y `ENTIDAD_NO_ENCONTRADA` en 13 sin inventar identificadores. En 15 eliminar
   el Jugador original y comprobar el GET 404 en 16.
8. Reiniciar desde la misma raíz: los registros y estados deben conservarse.
9. Abrir `http://localhost:8080/h2-console/`, driver `org.h2.Driver`, JDBC URL
   `jdbc:h2:file:./data/entidades`, usuario `sa` y contraseña vacía. Consultar
   `SELECT DTYPE, ID, NOMBRE, SALUD FROM ENTIDADES;`, sin modificar datos.

La evidencia de ejecuciones está en
[POO-08-VERIFICACION.md](POO-08-VERIFICACION.md). Los casos borde de nombres,
edad, experiencia y ciclo del creeper se prueban llamando al dominio directamente
en `InvariantesDominioTests`.

## Recorrido de defensa de cinco a siete minutos

| Minuto | Qué mostrar |
| --- | --- |
| 0–1 | Commit final, README, bitácora, licencia Apache 2.0 y arranque |
| 1–2 | POST, GET y PUT con GET posterior en Insomnia |
| 2–3 | Entidad abstracta, sobreescritura, sobrecarga y UICapable |
| 3–5 | Crear participantes, simular y consultar sus estados persistidos |
| 5–6 | POST ilegal, las dos excepciones y DELETE con GET posterior |
| 6–7 | H2, invariantes y respuesta breve de diseño |

Respuesta de diseño: el controller recibe HTTP; el service coordina acceso a
datos y transacciones; el dominio valida antes de cambiar estado. Los campos
privados y los métodos de negocio evitan asignaciones libres. Las pruebas
directas muestran que las reglas también se sostienen sin el controller.

La calificación y la defensa corresponden al docente. No se puede acreditar
puntualidad ni entrega en Classroom solo con los archivos locales.
