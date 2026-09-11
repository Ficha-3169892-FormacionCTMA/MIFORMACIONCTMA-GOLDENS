# Mi Formación CTMA

## Descripción del proyecto
Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

## Problema
Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

## Usuarios
1. **Aprendiz**: Necesidad de consultar actividades, registrar evidencias y hacer seguimiento a su progreso académico.
2. **Instructor**: Necesidad de publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

## Historias de Usuario
### Historia 1
**Como** aprendiz, **quiero** consultar mis actividades pendientes, **para** organizar mejor mi tiempo y cumplir con las fechas establecidas.
* **Criterio de aceptación**: La aplicación debe mostrar una lista con las actividades registradas. El aprendiz puede visualizar el nombre y la fecha de cada actividad.

### Historia 2
**Como** instructor, **quiero** publicar nuevas actividades, **para** que los aprendices puedan consultarlas desde la aplicación.
* **Criterio de aceptación**: El instructor puede crear una nueva actividad. La actividad debe quedar disponible para ser consultada por los aprendices.

### Historia 3
**Como** aprendiz, **quiero** registrar las evidencias de las actividades realizadas, **para** llevar un control de mi progreso durante la formación.
* **Criterio de aceptación**: El aprendiz puede marcar una actividad como completada. La aplicación debe reflejar el cambio de estado de la actividad.

## Tecnologías utilizadas
* Kotlin
* Android Studio
* Jetpack Compose
* Material Design 3
* Gradle
* Git
* GitHub

## Estado del proyecto
🚧 **Proyecto en desarrollo**

### Semana 2 - Cambios realizados
* Se creó el paquete `model`.
* Se implementó la clase `ActividadFormativa`.
* Se creó el enum `Prioridad`.
* Se desarrollaron las reglas de negocio.
* Se implementó la búsqueda de actividades.
* Se calcula el promedio de progreso.
* Se muestran datos calculados en la pantalla inicial.

### Semana 3 - Cambios realizados
1. **Interfaz de Usuario con Material 3**: Centralización de colores, tipografías y formas en el paquete `ui/theme`. Uso de `ColorScheme`, `Typography` y `Shapes`.
2. **Componentes Reutilizables**:
    * `EncabezadoFormacion`: Muestra el saludo y resumen de actividades.
    * `TarjetaActividad`: Componente stateless para detalles de actividad.
    * **Accesibilidad**: Uso de `contentDescription` y modificadores semánticos.
3. **Pantalla Principal y Layout Adaptable**:
    * Diseño Adaptable con `BoxWithConstraints` (alternando entre `LazyColumn` y `LazyVerticalGrid`).
    * Gestión de estados de lista vacía y optimización con `key`.

---

# Semana 7: Concurrencia y Estado Reactivo

## 🏗️ Arquitectura de Datos Reactiva
La aplicación implementa un flujo de datos unidireccional (UDF) y reactivo siguiendo este esquema:

**Room (DB) / DataStore (Prefs)**  
⬇️  
**Repository** (Transforma Entidades -> Modelos de Dominio)  
⬇️  
**ViewModel** (Combina Flows con `combine` y `flatMapLatest`)  
⬇️  
**Compose UI** (Recolecta con `collectAsStateWithLifecycle`)

### ⚡ Decisión de Dispatchers
- **Dispatchers.Main:** Utilizado en el ViewModel para la recolección de estados y actualización de la UI.
- **Dispatchers.IO:** Utilizado exclusivamente en el `AppRepository` mediante `withContext(Dispatchers.IO)` para todas las operaciones de persistencia (Room y DataStore), garantizando que las funciones sean **main-safe**.

---

# Semana 8: Servicios Web, Caché y Resiliencia (Android)

## 🌐 Arquitectura Offline-First
Para garantizar que el aprendiz pueda consultar sus actividades incluso sin conexión, se ha implementado un esquema de **Fuente Única de Verdad (SSOT)**:
1. La **API** provee los datos crudos (DTOs).
2. El **Repositorio** los procesa y persiste en **Room**.
3. La **UI** solo observa a Room a través de Flows.

### 🛡️ Estrategias de Resiliencia
- **Persistencia en Fallos:** Si la API falla (Timeout, 500), se capturan las excepciones y se mantiene el último caché válido en Room. La UI muestra un estado de error de actualización sin perder los datos previos.
- **Transacciones Atómicas:** El guardado de datos remotos se realiza en transacciones de Room para evitar estados inconsistentes.

### 🚀 Ejecución y Verificación (Android)
```bash
# Correr todas las pruebas unitarias (Semana 8)
./gradlew test

# Ejecutar tests del Repositorio
./gradlew :app:testDebugUnitTest --tests "com.samuel.miformacionctma.data.repository.*"
```

---

# 📦 Proyecto Complementario: EntregaSegura (Node.js/API)

### 🚀 Ejecución de Pruebas Automatizadas
Para configurar el entorno y verificar la suite de pruebas:

```bash
# 1. Instalación
npm install -D vitest @vitest/coverage-v8 supertest

# 2. Ejecutar pruebas (Vitest)
npm run test

# 3. Reporte de cobertura
npm run test:coverage
```

### 🧪 Estrategia de Calidad (Shift-Left)
| Requisito | Riesgo | Nivel | Test Automatizado |
| :--- | :--- | :--- | :--- |
| Trazabilidad estados | Cambio ilegal | Unidad | `orders.spec.ts` -> Matriz |
| Entrega con evidencia | Fraude | Unidad/TDD | `orders.spec.ts` -> EVIDENCE_REQUIRED |
| Seguridad API | Acceso no autorizado | API | `orders.api.spec.ts` -> POST /login |

#### Definition of Done (DoD)
- [ ] Suite de pruebas en verde (Sin Flaky Tests).
- [ ] Cobertura de ramas superior al 80%.
- [ ] Cero credenciales reales en Git (Tokens sintéticos).
