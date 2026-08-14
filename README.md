# Mi Formación CTMA

## Descripción del proyecto

Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

---

# Problema

Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

---

# Usuarios

## 1. Aprendiz

**Necesidad:**
Consultar actividades, registrar evidencias y hacer seguimiento a su progreso académico.

## 2. Instructor

**Necesidad:**
Publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

---

# Historias de Usuario

## Historia 1

**Como** aprendiz,

**quiero** consultar mis actividades pendientes,

**para** organizar mejor mi tiempo y cumplir con las fechas establecidas.

### Criterio de aceptación

- La aplicación debe mostrar una lista con las actividades registradas.
- El aprendiz puede visualizar el nombre y la fecha de cada actividad.

---

## Historia 2

**Como** instructor,

**quiero** publicar nuevas actividades,

**para** que los aprendices puedan consultarlas desde la aplicación.

### Criterio de aceptación

- El instructor puede crear una nueva actividad.
- La actividad debe quedar disponible para ser consultada por los aprendices.

---

## Historia 3

**Como** aprendiz,

**quiero** registrar las evidencias de las actividades realizadas,

**para** llevar un control de mi progreso durante la formación.

### Criterio de aceptación

- El aprendiz puede marcar una actividad como completada.
- La aplicación debe reflejar el cambio de estado de la actividad.

---

# Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Gradle
- Git
- GitHub

---

# Estado del proyecto

🚧 Proyecto en desarrollo - Semana 1

Actualmente la aplicación cuenta con:

- Configuración del entorno Android.
- Proyecto creado con Jetpack Compose.
- Pantalla inicial.
- Repositorio Git.
- Documentación inicial del proyecto.

Las siguientes semanas incorporarán nuevas funcionalidades como gestión de actividades, almacenamiento de datos y navegación entre pantallas.

# Semana 2

## Cambios realizados

- Se creó el paquete model.
- Se implementó la clase ActividadFormativa.
- Se creó el enum Prioridad.
- Se desarrollaron las reglas de negocio.
- Se implementó la búsqueda de actividades.
- Se calcula el promedio de progreso.
- Se muestran datos calculados en la pantalla inicial.

## 🧪 Guía 3: Documentación de Pruebas, Calidad y Verificación

Este apartado contiene la documentación completa de las actividades de calidad, diseño sistemático de casos de prueba y reporte de defectos desarrollados durante la Semana 3.

---

### 📍 Paso 1: Actividad de Activación y Enlace

* **Historia de Usuario Prioritaria:** `HU-03` — Confirmación de Entrega de Actividad Formativa con Evidencia.
* **Criterio de Aceptación Seleccionado (`CA-01`):** El sistema debe permitir al aprendiz adjuntar un enlace de evidencia (GitHub/Drive), validar que no esté vacío y cambiar el estado de la actividad a `ENTREGADA` ajustando el progreso al `100%`.
* **Riesgo Asociado (`R-02`):** Permitir entregas con enlaces vacíos o malformados, o fallar en la actualización del estado y progreso en la interfaz gráfica.
* **Prioridad de Prueba:** Alta (Core de negocio formativo SENA).

#### Preguntas Previas a la Ejecución:
1. **Datos:** ¿Qué esquema de URL es permitido (`http://`, `https://`) y cómo responde el sistema ante espacios en blanco (`"   "`)?
2. **Permisos y Estado:** ¿Puede una actividad en estado `ENTREGADA` o `COMPLETADA` re-entregarse si el plazo expiró (`diasRestantes < 0`)?
3. **Ambiente:** ¿El cambio de estado debe persistirse en memoria local (ViewModel/State) o requiere confirmación asíncrona de red?

#### Escenarios Propuestos:
* **Escenario Positivo (Aprobar):**
    * *Dado que* la actividad #2 está en estado `EN_PROCESO` con progreso 90%, *cuando* el aprendiz ingresa la URL `https://github.com/paboncito666/GuiaAndroid1.git` y presiona "Confirmar Entrega", *entonces* el estado cambia a `ENTREGADA`, el progreso pasa a `100%` y los días restantes se mantienen.
* **Escenario Negativo (Rechazar):**
    * *Dado que* la actividad #2 está en estado `EN_PROCESO`, *cuando* el aprendiz deja el campo de URL vacío o ingresa `"   "` y presiona "Confirmar Entrega", *entonces* el sistema muestra el mensaje de error "La URL de evidencia es obligatoria" y el estado se mantiene en `EN_PROCESO` con 90% de progreso.

* **Columnas a Actualizar en Matriz de Trazabilidad:** `ID_Caso_Prueba`, `Estado_Ejecucion` (PASS/FAIL), `ID_Defecto_Asociado` y `Fecha_Ultima_Ejecucion`.

---

### 📚 Paso 2: Apropiación Conceptual

* **Conversión de Criterios a Casos:** Un Criterio de Aceptación es una regla de negocio que se transforma en un Caso de Prueba al definirle precondiciones explícitas, datos sintéticos de entrada y un resultado esperado observable.
* **Pruebas Positivas vs. Negativas:**
    * *Positivas:* Verifican el comportamiento del sistema ante datos y flujos válidos.
    * *Negativas:* Validan el manejo de errores, datos fuera de rango y respuestas controladas del sistema.
* **Técnicas de Caja Negra Aplicadas:**
    1. *Partición de Equivalencia (PE):* División de campos de entrada en clases válidas e inválidas.
    2. *Análisis de Valores Límite (AVL):* Evaluación de fronteras exactas y sus vecinos (0, 100, -1, 101).
    3. *Tablas de Decisión (TD):* Modelado de reglas complejas con combinaciones de condiciones y acciones.
    4. *Transición de Estados (TE):* Evaluación del ciclo de vida del objeto (`PENDIENTE` → `EN_PROCESO` → `ENTREGADA` → `CALIFICADA`).
    5. *Escenarios de Caso de Uso (CU):* Flujos principales, alternos y excepcionales desde la experiencia de usuario.
* **Ciclo de Vida del Defecto:** Detectado → Reportado → Asignado → En Corrección → Re-probado → Cerrado.
* **Severidad vs. Prioridad:**
    * *Severidad:* Nivel de impacto técnico en la estabilidad o funcionamiento del sistema.
    * *Prioridad:* Grado de urgencia del negocio para resolver el fallo.

---

### 🎯 Paso 3: Laboratorio 1 - Diseño Sistemático de Casos de Prueba

Se diseñaron **12 casos de prueba** derivados de la historia `HU-03`, asegurando cobertura técnica con las 5 metodologías de caja negra:

| ID Caso | Ref. HU/CA | Técnica | Tipo | Precondición | Datos Sintéticos (Entrada) | Resultado Esperado Observable | Prioridad |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CP-01** | HU-03/CA-01 | PE | Positivo | Actividad id=2 en proceso | `progreso = 50`, `url = "https://github.com/test"` | Progreso asignado en 50%, estado "En Proceso". | Alta |
| **CP-02** | HU-03/CA-01 | AVL | Positivo | Actividad id=1 en límite inferior | `progreso = 0` | Progreso en 0%, indicador en 0.0f, estado "Pendiente". | Media |
| **CP-03** | HU-03/CA-01 | AVL | Positivo | Actividad id=1 en límite superior | `progreso = 100` | Progreso en 100%, indicador en 1.0f, estado "Entregada/Completada". | Alta |
| **CP-04** | HU-03/CA-01 | PE | Negativo | Actividad id=4 existente | `progreso = -15` | El sistema ajusta (`coerceIn`) o rechaza el valor a 0% sin romper la UI. | Alta |
| **CP-05** | HU-03/CA-01 | PE | Negativo | Actividad id=4 existente | `progreso = 120` | El sistema ajusta (`coerceIn`) a 100% o lanza error de validación. | Alta |
| **CP-06** | HU-03/CA-01 | AVL | Negativo | Actividad id=2 existente | `progreso = -1` (Vecino inferior de 0) | Validación falla o se ajusta a 0%. No se permite valor negativo. | Media |
| **CP-07** | HU-03/CA-01 | AVL | Negativo | Actividad id=2 existente | `progreso = 101` (Vecino superior de 100) | Validación falla o se ajusta a 100%. No desborda el indicador. | Media |
| **CP-08** | HU-03/CA-01 | TD | Positivo | Actividad en plazo y URL válida | `diasRestantes = 2`, `url = "http://sena.edu.co"` | Entrega exitosa. Estado pasa a `ENTREGADA`. | Alta |
| **CP-09** | HU-03/CA-01 | TD | Negativo | Actividad en plazo y URL vacía | `diasRestantes = 2`, `url = ""` | Entrega bloqueada. Mensaje "URL obligatoria". | Alta |
| **CP-10** | HU-03/CA-01 | TE | Positivo | Actividad estado `PENDIENTE` | Evento: `IniciarActividad` | Estado transiciona a `EN_PROCESO`. | Media |
| **CP-11** | HU-03/CA-01 | TE | Negativo | Actividad estado `PENDIENTE` | Evento: `CalificarActividad` | Transición inválida. Operación bloqueada (requiere estar Entregada). | Alta |
| **CP-12** | HU-03/CA-01 | CU | Positivo | Lista de actividades cargada | Flujo Principal: Seleccionar Actividad #2 → Adjuntar URL → Confirmar | La tarjeta en la lista actualiza su chip y progreso a 100%. | Alta |

---

### 📊 Paso 4: Laboratorio 2 - Tabla de Decisión y Transición de Estados

#### 1. Tabla de Decisión (Validación de Entrega)
* **C1:** ¿La URL de evidencia es válida y no vacía?
* **C2:** ¿Los días restantes son ≥ 0 (Dentro de plazo)?
* **C3:** ¿El progreso actual es < 100%?

| Regla | C1 (URL Válida) | C2 (En Plazo) | C3 (Progreso < 100%) | Acción Resultante | ID Caso Derivado |
| :---: | :---: | :---: | :---: | :--- | :---: |
| **R1** | V | V | V | **Aprobar Entrega:** Estado = `ENTREGADA`, Progreso = 100% | `CP-08` |
| **R2** | F | - | - | **Rechazar Entrega:** Mostrar error "URL requerida" | `CP-09` |
| **R3** | V | F | V | **Entrega Tardía:** Estado = `ENTREGADA_FUERA_DE_TIEMPO` | `CP-13` |
| **R4** | V | V | F | **Rechazar Entrega:** La actividad ya está completada | `CP-14` |

> **Justificación de casilla "no importa" (-):** En la Regla 2 (R2), al ser la URL inválida o vacía, la condición de plazo o de progreso actual pasa a ser irrelevante, ya que el sistema rechaza automáticamente la acción.

#### 2. Modelo de Transición de Estados
* **Estados Permitidos:** `PENDIENTE`, `EN_PROCESO`, `ENTREGADA`, `CALIFICADA`.
* **Transiciones Válidas:**
    * `PENDIENTE` → `EN_PROCESO` (Iniciar actividad)
    * `EN_PROCESO` → `ENTREGADA` (Adjuntar evidencia)
    * `ENTREGADA` → `CALIFICADA` (Revisión de instructor)
* **Transiciones Inválidas Marcadas:**
    1. `PENDIENTE` → `CALIFICADA` (**INVÁLIDA**: No se puede calificar una actividad sin evidencia entregada).
    2. `CALIFICADA` → `ENTREGADA` (**INVÁLIDA**: No se permite re-entregar actividades que ya han sido evaluadas).

---

### 🐞 Paso 5: Laboratorio 3 - Ejecución Simulada y Gestión de Defectos

#### 1. Resultados de Casos Simulados (SIM-01 a SIM-05)

| Simulación | Caso Evaluado | Resultado Observado | Decisión | Justificación Técnica |
| :---: | :--- | :--- | :---: | :--- |
| **SIM-01** | Transición directa de `PENDIENTE` a `CALIFICADA` | El sistema permitió asignar nota 5.0 sin evidencia subida. | **FAIL** | Violación de regla de negocio y transición de estados inválida. |
| **SIM-02** | Progreso en 100% | La tarjeta muestra indicador al 100% pero el texto dice "Pendiente". | **FAIL** | Inconsistencia de estado visual vs. dato de progreso. |
| **SIM-03** | Días restantes = -2 | La UI muestra "Vencida hace 2 día(s)". | **PASS** | Formateo correcto de casos límite negativos en `textoDiasRestantes`. |
| **SIM-04** | Entrega con URL vacía (`""`) | El sistema acepta la entrega y cambia estado a `ENTREGADA`. | **FAIL** | No se aplica la validación de evidencia obligatoria. |
| **SIM-05** | Filtro por Prioridad `ALTA` | La lista filtra correctamente las actividades 2, 4, 6 y 10. | **PASS** | Comportamiento esperado de la colección. |

#### 2. Registro Mínimo de Defectos Detectados

| ID Defecto | Caso Origen | Título Resumido del Defecto | Severidad | Prioridad | Estado |
| :---: | :---: | :--- | :---: | :---: | :---: |
| **DEF-01** | SIM-01 | Permite calificar actividad en estado `PENDIENTE` sin evidencia. | Crítica | Alta | Abierto |
| **DEF-02** | SIM-04 | Permite confirmar entrega de actividad con URL de evidencia vacía. | Alta | Alta | Abierto |
| **DEF-03** | SIM-02 | Inconsistencia gráfica: Progreso al 100% mantiene etiqueta "Pendiente". | Media | Media | Abierto |

#### 3. Reporte Completo y Reproducible de Defecto (DEF-02)


================================================================================
REPORTE DE DEFECTO / BUG REPORT
================================================================================
ID Defecto:        DEF-02
Título:            Permite confirmar la entrega de evidencia con el campo URL vacío o compuesto por espacios
Proyecto:          Mi Formación CTMA - Módulo de Actividades Formativas
Componente:        PantallaActividades / ModalEntregaEvidencia
Versión:           v1.0.0-alpha
Ambiente:          Emulador Android Pixel 6 - API 34 / Android 14

SEVERIDAD:         Alta (Falla de regla de negocio principal)
PRIORIDAD:         Alta (Afecta el flujo de entregas del aprendiz)

PRECONDICIONES:
1. La aplicación está iniciada con los datos de prueba cargados.
2. Existe al menos una actividad en estado EN_PROCESO (Ejemplo: id=2 "Variables y tipos de datos").

PASOS PARA REPRODUCIR:
1. Abrir la aplicación "Mi Formación CTMA".
2. Ubicar la tarjeta de la actividad "Variables y tipos de datos" (id=2).
3. Hacer clic sobre la tarjeta para abrir el diálogo de confirmación de entrega.
4. En el campo de texto "URL de la Evidencia (GitHub / Drive)", ingresar espacios en blanco: "   " o dejarlo completamente vacío.
5. Presionar el botón "Confirmar Entrega".

RESULTADO ESPERADO:
El sistema debe validar la entrada, mostrar un mensaje de error indicando "La URL de evidencia es requerida" y mantener la actividad en estado EN_PROCESO con su progreso actual.

RESULTADO REAL:
El sistema acepta la confirmación, cierra el diálogo, cambia el estado visual a "ENTREGADA" y asigna el progreso al 100% sin exigir una URL válida.

DATOS SINTÉTICOS UTILIZADOS:
- Actividad: ID=2, Titulo="Variables y tipos de datos", Progreso Inicial=90%
- Campo URL enviado: "" (cadena vacía)

ANEXO DE EVIDENCIA SIMULADA:
[Logcat] INFO: Actividad 2 actualizada a estado ENTREGADA con evidencia URL: ""
================================================================================



---

### Cierre de la Guía (Ticket de Salida)

#### 1. ¿Cómo seleccionar la técnica de prueba adecuada?
* **Partición de Equivalencia y Análisis de Valores Límite:** Se aplican cuando probamos entradas numéricas o rangos (ej. porcentaje de progreso de 0 a 100).
* **Tablas de Decisión:** Se utilizan cuando existen combinaciones de múltiples reglas de negocio (ej. validación de plazo + URL de evidencia).
* **Transición de Estados:** Se usa para validar el ciclo de vida del objeto y asegurar cambios de estado válidos (ej. `PENDIENTE` → `EN_PROCESO` → `ENTREGADA` → `CALIFICADA`).
* **Escenarios de Casos de Uso:** Se emplean para evaluar la experiencia de usuario cubriendo el flujo principal, flujos alternos y excepciones.

#### 2. Identificación de un dato límite
En el atributo `progreso` (cuyo rango válido es de 0 a 100):
* **Límite exacto válido:** `100`
* **Vecino límite inválido:** `101` (evalúa que el sistema no permita desbordamientos).

#### 3. Diferencia entre Severidad y Prioridad
* **Severidad:** Mide el **impacto técnico** del fallo en el sistema (ej. si la app se cierra inesperadamente o corrompe datos, es de Severidad Crítica).
* **Prioridad:** Mide la **urgencia de negocio** para resolverlo (ej. un error de tipografía en el logo principal tiene Severidad Baja pero Prioridad Alta).

#### 4. Propuesta de mejora a un caso de prueba
Al caso de prueba `CP-08` (Confirmación de Entrega de Evidencia), sugiero agregar una validación mediante expresión regular (`Regex`) que compruebe que la URL ingresada pertenezca a un dominio válido de repositorio o almacenamiento (ej. `github.com` o `drive.google.com`), evitando el ingreso de enlaces genéricos.