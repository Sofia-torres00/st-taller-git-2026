# Bitácora de asistencia con IA

## Herramientas usadas

1. Codex

   - Marca: Codex / OpenAI
   - Modelo exacto:  GPT-6.1 Sol

## Resumen de uso

Usé IA para revisar el enunciado y la rúbrica de POO-06, comparar los requisitos
con la estructura que ya tenía mi proyecto de Minecraft y detectar qué faltaba.

Con esa ayuda reorganicé el dominio en `py.edu.uc.lp3.domain`, los controllers
en `py.edu.uc.lp3.rest.controller` y Application en `py.edu.uc.lp3`, actualizando
los packages e imports sin perder funcionalidad.

También agregué el constructor sobrecargado de Zombie y la sobrecarga de
`Entidad.mover`. Revisé la sobreescritura que ya existía de
`describirComportamiento()` en Zombie y Creeper y cómo el controller las usa
como Entidad. Después actualicé el README y el Mermaid según el código final.

Usé IA para detectar y corregir los problemas de configuración de Java 21 y
del Maven Wrapper. Verifiqué compilación, tests, arranque y endpoints REST,
incluyendo que una velocidad negativa devuelva HTTP 400 y que la validación
siga en el dominio.

## Prompts resumidos

- "Revisar el proyecto contra el enunciado POO-06 y detectar qué faltaba."
- "Reorganizar los paquetes según el template sin perder funcionalidad."
- "Agregar constructores y métodos sobrecargados manteniendo objetos válidos."
- "Verificar sobreescritura y polimorfismo con Entidad, Zombie y Creeper."
- "Corregir la respuesta REST para valores inválidos sin duplicar la validación."
- "Probar Maven Wrapper, Java 21, tests y endpoints REST."
- "Actualizar README y Mermaid según el código final."

Nota: revisé las decisiones y las verificaciones finales antes de versionar
los cambios. La IA fue una ayuda para el trabajo; la revisión final quedó a mi cargo.

## POO-08 — 10/11/2026

Se auditó el proyecto raíz antes de editar: ya existían JPA/H2 en archivo,
CRUD, ocho subclases persistidas, migración, simulación y la interface
`UICapable`. Se conservaron POO-06/07, las propiedades y los repositories.
Se completaron los faltantes: reglas radiales y validación del creeper en el
dominio, jerarquía de excepciones con handlers por tipo y códigos de error,
colección Insomnia, documentación y verificaciones de persistencia.

### Herramientas utilizadas en esta etapa

Registro inicial conservado. Para GitHub Copilot, el dato vigente confirmado
en el cierre formal es **GPT-6.1 Sol High**; el valor anterior de esta lista se
mantiene únicamente como historial.

- ChatGPT / OpenAI: utilizado para esta revisión y el desarrollo de POO-08;
  uso de ChatGPT por la compañera confirmado en esta sesión.
  Modelo exacto: gpt 6.1 sol
- Codex / OpenAI: inspección y edición del código, ejecución de Maven con
  Java 21 y pruebas HTTP/H2. Se conserva arriba el registro de la etapa POO-06;
  el nombre de esta etapa fue confirmado por el usuario según su interfaz.
  Modelo exacto: gpt 6.1 sol
- GitHub Copilot: uso por la compañera confirmado en esta sesión; no se
  atribuyen a Copilot las ejecuciones de herramientas realizadas por Codex.
  Modelo exacto: fusion standard (nombre informado por el usuario)

### Prompts resumidos de POO-08

- Revisar el estado real contra POO-08 antes de modificar lo que ya funciona.
- Verificar JPA/H2, constructores protegidos, herencia y archivos ignorados.
- Comprobar CRUD completo con IDs generados, sin listas en memoria.
- Demostrar actualización, rechazo de datos inválidos y persistencia con GET.
- Mantener las dos reglas de explosión en el dominio y coordinarlas desde el service.
- Completar base y dos excepciones hijas con handlers y JSON distinguible.
- Crear una colección importable de Insomnia, con variables y orden de ejecución.
- Completar README, Mermaid y respuestas aplicadas a Minecraft.
- Ejecutar clean test, pruebas HTTP reales, consola H2 y reinicio de la aplicación.

No se pegaron conversaciones completas. No se usó ni eliminó `bin/` y no se
prepararon archivos en staging, commits ni pushes. Los resultados verificables
se registran en `docs/POO-08-VERIFICACION.md` para revisión antes de versionar.

### Revisión adicional con la rúbrica CYT646

Se contrastó la entrega con los ocho criterios de la rúbrica proporcionada por
el usuario. La pregunta sobre estados imposibles permitió detectar y reproducir
cinco casos borde directamente en el dominio: nombre demasiado largo, edad
desbordada, experiencia desbordada, reencendido sin mecha y muerte por veneno
durante la cuenta regresiva. Se corrigieron sin trasladar reglas al controller.

Prompts resumidos adicionales:

- Contrastar POO-08 con los ocho criterios y distinguir código verificado de entrega pendiente.
- Probar invariantes llamando al dominio sin pasar por HTTP ni por el service.
- Cerrar los desbordamientos y conservar el estado cuando se rechaza una operación.
- Preparar especificaciones de Minecraft y un recorrido de defensa de cinco a siete minutos.

El usuario confirmó el nombre `gpt 6.1 sol` para ChatGPT y Codex, y posteriormente
indicó `fusion standard` para GitHub Copilot. Se registran esos nombres tal como
fueron informados, sin atribuir a Copilot el modelo de las otras herramientas.
El commit final, su enlace completo, la entrega en Classroom y la puntualidad
todavía no pueden acreditarse. Se mantiene la instrucción de no hacer staging,
commit ni push antes de la revisión del usuario.

Resultado posterior a las correcciones: `clean test` con Java 21 ejecutó
70 tests, con 0 fallos, 0 errores y 0 omitidos. Se comprobó el arranque de la
versión corregida, sus consultas de lectura y el rechazo HTTP de un nombre de
101 caracteres mediante el endpoint que construye un Zombie directamente.


### Cierre formal de POO-08 — 10/11/2026

GitHub Copilot

Modelo exacto: GPT-6.1 Sol High

Esta es la última información confirmada por el usuario y sustituye, para el
registro vigente, el dato «fusion standard» informado anteriormente. Se conserva
el texto previo como historial; no se deduce un modelo distinto por cuenta propia.

Resumen de esta etapa, en frases propias:

- Auditoría contra la rúbrica: se contrastaron los ocho criterios con archivos y evidencia reales.
- H2/JPA: se verificaron herencia SINGLE_TABLE, UUID y persistencia en archivo sin cambiar la configuración válida.
- CRUD persistente: se comprobaron crear, consultar, actualizar y eliminar, con GET posteriores.
- Interface de dominio: se reutilizó UICapable en el endpoint de sonidos.
- Simulación de explosión: se comprobaron daño radial y consumo del Creeper con estado persistido.
- Excepciones y handlers: se verificaron las dos hijas y sus respuestas 404/409 distinguibles.
- Invariantes: se probaron reglas del dominio y valores borde, sin trasladarlas al controller.
- Colección Insomnia: se organizó un flujo de 16 etapas con subrequests, Environment Local y captura de UUID mediante scripts exportados; se documentó la alternativa manual.
- Pruebas finales: se repitió clean test y se verificó el recorrido de la colección; los resultados se consignan en los informes de verificación.

Este cierre se limita a documentación, colección y verificaciones. No modifica
código funcional y no ejecuta git add, commit ni push.

Verificación del cierre: clean test volvió a ejecutar 70 tests con 0 fallos,
0 errores y 0 omitidos. El flujo actualizado de Insomnia recorrió 25 peticiones
HTTP en 16 etapas y pasó 43 comprobaciones de los scripts exportados, con UUID
capturados desde los POST. El importador oficial aceptó los 44 recursos. El
servidor de prueba se detuvo al finalizar. Evidencia en
`docs/insomnia/VERIFICACION-FLUJO.md`.
