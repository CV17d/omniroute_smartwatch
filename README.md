# OmniRoute — Smartwatch Clients (watchOS & Wear OS Standalone)

Clientes nativos para Smartwatches de **OmniRoute** diseñados como asistente logístico ágil para repartidores de mensajería y paquetería en motocicleta y bicicleta, con interacciones ultrarrápidas de **3 a 5 segundos**.

---

## 1. Diseño Glanceable (Modo Claro de Alto Contraste)

La interfaz de usuario está optimizada para legibilidad instantánea bajo luz solar directa en exteriores:

| Pantalla | watchOS (Apple Watch) | Wear OS (Circular) | Descripción |
| :--- | :--- | :--- | :--- |
| **Pantalla 1 (Inicio)** | `RouteOverviewStartView` | `RouteStartScreen` | Mapa vectorial con ruta activa en verde, waypoints, chip de progreso `4/15 ENTREGAS` y botón masivo `INICIAR RUTA`. |
| **Pantalla 2 (Navegación)** | `ActiveDeliveryView` | `ActiveDeliveryScreen` | Indicador de giro `↓ 150m - Continuar abajo`, badge de vehículo `MOTO`, dirección principal `CALLE 22 # 5-43`, datos de destinatario `Envia • Apt 302`, botón verde `¡LLEGUE!` y acceso a `INCIDENCIA ⚠️`. |
| **Pantalla 3 (Confirmación)** | `DeliveryConfirmationModal` | `DeliveryConfirmationScreen` | Confirmación rápida con `ENTREGAR PAQUETE`, dirección y botones masivos: `✓ ENTREGADO` y `✗ NO ENTREGADO / AUSENTE`. |

---

## 2. Arquitectura Wear OS Standalone (Kotlin / Compose / Retrofit / Room / WorkManager)

La aplicación Wear OS es **100% Standalone** (`standalone="true"`). El reloj inteligente se comunica directamente con el backend cloud (Spring Boot / NestJS) mediante **Wi-Fi / LTE**, sin requerir teléfono móvil intermediario.

### Componentes de la Capa de Datos (Data Layer):
1. **Conectividad REST Directa (Retrofit + OkHttp):**
   - [`RouteApiService.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/remote/RouteApiService.kt):
     - `GET /api/routes/active`: Descarga directa de la ruta activa y sus paradas.
     - `POST /api/deliveries/{id}/status`: Envío directo de confirmaciones de entrega ("DELIVERED", "ABSENT", "INCIDENT").
   - [`ApiClient.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/remote/ApiClient.kt): Cliente OkHttp con timeouts optimizados para wearables (10s) y logging interceptor.
2. **Persistencia Local y Cola Offline (Room Database):**
   - [`RouteDatabase.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/local/database/RouteDatabase.kt): Base de datos SQLite reactiva.
   - Entidades:
     - `DeliveryStopEntity`: Almacena las paradas de la ruta activa localmente para renderizado offline instantáneo.
     - `RouteSummaryEntity`: Resumen y progreso de la ruta.
     - `PendingDeliverySyncEntity`: Cola transaccional de peticiones `POST` fallidas en caso de pérdida de señal Wi-Fi/LTE.
   - [`DeliveryDao.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/local/dao/DeliveryDao.kt): Consultas reactivas vía `Flow` y gestión de la cola offline.
3. **Sincronización Diferida en Segundo Plano (WorkManager):**
   - [`SyncPendingDeliveriesWorker.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/worker/SyncPendingDeliveriesWorker.kt): Tarea en segundo plano con restricción `NetworkType.CONNECTED` que drena y envía las confirmaciones encoladas en Room una vez se restablece la conexión Wi-Fi/LTE.
4. **Repositorio Unificado:**
   - [`DeliveryRepository.kt`](file:///c:/Users/CRISTIAN/Documents/chicken/app_smartwatch/app/src/main/java/com/omniroute/wear/data/repository/DeliveryRepository.kt): Monitorea la red mediante `ConnectivityManager`, prioriza escritura local inmediata en Room (3 segundos) y despacha la petición HTTP directa o encola para WorkManager.

---

## 3. Estructura del Proyecto Wear OS

```text
app/src/main/java/com/omniroute/wear/
├── MainActivity.kt                            # Entry point, Wear Scaffold, TimeText y Háptica
├── ui/
│   ├── theme/
│   │   ├── Color.kt                           # Tokens Modo Claro (Verde #22B061, Rojo #E03838)
│   │   ├── Type.kt                            # Tipografía glanceable Wear OS
│   │   └── Theme.kt                           # OmniWearTheme
│   ├── components/
│   │   ├── DeliveryActionButton.kt            # Botones masivos con safe insets para pantallas redondas
│   │   ├── CircularRouteProgress.kt           # Chip 4/15 con CircularProgressIndicator nativo
│   │   ├── AddressCard.kt                     # Tarjeta de dirección curva
│   │   ├── StatusBadge.kt                     # Tag MOTO / estado
│   │   └── WearMiniMap.kt                     # Canvas vectorial de ruta
│   └── screens/
│       ├── RouteStartScreen.kt                # Pantalla 1 con ScalingLazyColumn
│       ├── ActiveDeliveryScreen.kt            # Pantalla 2 con ScalingLazyColumn
│       ├── DeliveryConfirmationScreen.kt      # Pantalla 3 con ScalingLazyColumn
│       ├── IncidentDialog.kt                  # Selección rápida de incidencias
│       └── RouteFinishedScreen.kt             # Resumen de ruta completada
├── domain/
│   └── model/
│       ├── DeliveryStatus.kt                  # Estados PENDING, IN_PROGRESS, ARRIVED, DELIVERED, ABSENT, INCIDENT
│       ├── DeliveryStop.kt                    # Modelo de parada
│       └── RouteSummary.kt                    # DTO de resumen de ruta
├── data/
│   ├── remote/
│   │   ├── RouteApiService.kt                 # Retrofit REST: GET /api/routes/active, POST /api/deliveries/{id}/status
│   │   ├── ApiClient.kt                       # Configuración OkHttp + Retrofit
│   │   └── dto/
│   │       ├── DeliveryStatusDtos.kt          # Request y Response DTOs
│   │       └── RouteDtos.kt                   # DTOs y mappers a dominio
│   ├── local/
│   │   ├── database/RouteDatabase.kt          # Room Database
│   │   ├── dao/DeliveryDao.kt                 # Room DAO para paradas y cola offline
│   │   └── entity/
│   │       ├── DeliveryStopEntity.kt
│   │       ├── RouteSummaryEntity.kt
│   │       └── PendingDeliverySyncEntity.kt
│   ├── repository/
│   │   └── DeliveryRepository.kt              # Repositorio Clean Architecture (Room + Retrofit)
│   └── worker/
│       └── SyncPendingDeliveriesWorker.kt     # WorkManager para sincronización diferida
└── viewmodels/
    ├── DeliveryUiState.kt                     # Estado inmutable (isOnline, pendingSyncCount, route, screen)
    └── DeliveryRouteViewModel.kt              # ViewModel MVVM conectado al repositorio
```

---

## 4. Arquitectura watchOS (Apple Watch)
El target de watchOS reside en `OmniRouteWatch/`, estructurado para Apple Watch con SwiftUI y sincronización WCSession para iOS Companion.
