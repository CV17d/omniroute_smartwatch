# OmniRoute — watchOS Client

## 1. Definición del Proyecto
**OmniRoute** es una aplicación frontend para Apple Watch diseñada como asistente logístico ágil para repartidores de mensajería y paquetería (e.g., Servientrega, Envía).

* **Problema:** Los repartidores pierden tiempo crítico y comprometen su seguridad al manipular continuamente el teléfono móvil en cada parada.
* **Solución:** Una app en el Apple Watch que recibe la ruta optimizada (múltiples paradas) desde el backend, permitiendo:
  - Visualizar rápidamente la siguiente entrega.
  - Navegar a la parada actual.
  - Confirmar el estado de la entrega (**"Entregado"** o **"Ausente"**) con interacciones ultrarrápidas de **3 a 5 segundos**.
* **Directrices de Diseño (Glanceable UI/UX):**
  - **Modo Claro (Light Mode):** Alta legibilidad en exteriores bajo la luz solar directa.
  - **Acentos de color:** Verde amigable para acciones positivas / éxito, rojo para cancelaciones / ausente / alertas.
  - **Botones masivos y tipografía grande:** Áreas táctiles amplias adaptadas a la pantalla pequeña del reloj y al uso en movimiento.

---

## 2. Stack Tecnológico
* **Plataforma:** watchOS (Target mínimo: **watchOS 9.0+**).
* **UI Framework:** SwiftUI.
* **Arquitectura:** MVVM (Model-View-ViewModel) estricto con Clean Architecture.
* **Comunicación de Red:** Cliente REST / JSON asíncrono (`async/await`) sincronizado con backend NestJS.
* **Gestión de Estado:** `@StateObject`, `@ObservedObject`, `@EnvironmentObject` y `@Binding` según el alcance de cada componente.

---

## 3. Reglas Estrictas de Arquitectura y Código
1. **Componentización Extrema:**
   - Prohibido crear archivos "monstruo" con cientos de líneas.
   - Cada vista principal debe desglosarse en subcomponentes atómicos, modulares y testeables (e.g., `DeliveryButton`, `RouteMapView`, `ProgressHeader`, `StopCardView`).
2. **Separación de Responsabilidades (SoC):**
   - Las vistas SwiftUI solo declaran UI y reaccionan al estado del ViewModel.
   - Prohibida la lógica de negocio y llamadas de red directas dentro del cuerpo de las vistas.
   - ViewModels gestionan el estado de presentación e interactúan con Servicios/Casos de uso dedicados.
3. **Estructura de Directorios:**
   ```text
   OmniRoute/
   ├── Models/         # Entidades de dominio y DTOs de red
   ├── ViewModels/     # Estado de presentación y lógica de vistas
   ├── Views/          # Vistas principales y pantallas
   ├── Components/     # Sub-componentes UI reutilizables
   ├── Services/       # Servicios de red (API REST), ubicación y almacenamiento
   └── Utilities/      # Extensiones, constantes de diseño (colores, tipografías) y helpers
   ```
4. **Clean Code & Naming:**
   - Nomenclatura descriptiva en **inglés** para variables, tipos, métodos y archivos.
   - Uso estricto de modificadores de acceso (`private`, `fileprivate`, `public`, `internal`).
   - Inyección de dependencias a través de protocolos para facilitar pruebas y mocks.

---

## 4. Control de Versiones y Flujo de Trabajo
* **Commits Atómicos:** Un commit por cada unidad lógica de cambio, componente creado o refactorización.
* **Conventional Commits:**
  - `feat:` Nuevas funcionalidades o componentes UI.
  - `refactor:` Reestructuración de código sin cambio de comportamiento.
  - `fix:` Corrección de errores visuales o lógicos.
  - `docs:` Cambios en documentación.
  - `style:` Formato, espaciado o limpieza de estilos.
  - `test:` Creación o actualización de pruebas unitarias/UI.
