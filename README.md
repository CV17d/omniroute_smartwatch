# OmniRoute — Smartwatch (watchOS)

Cliente nativo para Apple Watch (watchOS 9.0+) diseñado como asistente logístico ágil para repartidores de mensajería y paquetería en motocicleta y bicicleta.

---

## 1. Diseño Glanceable (Clon Fiel de la Imagen de Referencia)

La interfaz de usuario está optimizada para interacciones ultrarrápidas de **3 a 5 segundos** bajo luz solar directa en exteriores:

| Pantalla | Componente | Descripción |
| :--- | :--- | :--- |
| **Watch 1 (Inicio)** | `RouteOverviewStartView` | Visualiza el mapa vectorial con la ruta activa en verde, waypoints, chip de progreso `4/15 ENTREGAS` con `CircularProgressRing`, y el botón masivo `INICIAR RUTA`. |
| **Watch 2 (Navegación)** | `ActiveDeliveryView` | Indicador de giro `↓ 150m - Continuar abajo`, badge de vehículo `MOTO`, dirección principal en alto contraste `CALLE 22 # 5-43`, datos de destinatario `Envia • Apt 302`, botón verde `¡LLEGUE!` y acceso rápido a `INCIDENCIA ⚠️`. |
| **Watch 3 (Confirmación)** | `DeliveryConfirmationModal` | Modal de confirmación rápida con `ENTREGAR PAQUETE`, dirección y botones masivos: `✓ ENTREGADO` y `✗ NO ENTREGADO / AUSENTE`. |

---

## 2. Arquitectura de Conectividad (WCSession & Bluetooth)

El reloj **NO consume endpoints REST directamente en la nube**. Se sincroniza localmente vía **Bluetooth** con la aplicación del teléfono móvil (iOS Companion):
- **Descarga de DTOs:** Recibe `RouteSummary` y `DeliveryStop` compactos y deserializados eficientemente.
- **Transmisión de Eventos:** Envía eventos de mutación `DeliveryStatusUpdateEvent` (`delivered`, `absent`, `incident`).
- **Resiliencia Offline:** En caso de pérdida de conexión Bluetooth, `LocalCacheManager` almacena las entregas en cola local y `ReachabilityObserver` alerta al usuario con `ConnectionLostBanner`, realizando un flush automático al reconectar.
- **Haptic Feedback:** Patrones hápticos táctiles (`WKInterfaceDevice`) para confirmación de entrega exitosa, alerta de ausencia y avisos de giro sin necesidad de mirar la pantalla.

---

## 3. Estructura del Proyecto

```text
OmniRouteWatch/
├── App/
│   └── OmniRouteWatchApp.swift               # Entrada principal @main y RootCoordinator
├── Models/
│   ├── DeliveryStop.swift                    # Entidad de parada y coordenadas geográficas
│   ├── DeliveryStatus.swift                  # Estados del ciclo de vida y eventos DTO
│   └── RouteSummary.swift                    # Resumen de ruta para transferencia BLE
├── ViewModels/
│   ├── DeliveryRouteViewModel.swift          # Estado de presentación y ciclo de vida
│   ├── DeliveryRouteViewModel+Navigation.swift
│   ├── DeliveryRouteViewModel+DeliveryActions.swift
│   ├── DeliveryRouteViewModel+AbsentActions.swift
│   ├── DeliveryRouteViewModel+Sync.swift
│   └── DeliveryRouteViewModel+OfflineQueue.swift
├── Views/
│   ├── RouteOverviewStartView.swift          # Pantalla Watch 1
│   ├── ActiveDeliveryView.swift              # Pantalla Watch 2
│   ├── DeliveryConfirmationModal.swift       # Pantalla Watch 3
│   ├── RouteOverviewListView.swift           # Lista deslizable de paradas
│   ├── RouteFinishedSummaryView.swift        # Resumen final de ruta completada
│   ├── IncidentReportSheet.swift             # Modal de reporte rápido de incidencias
│   └── ConnectionLostBanner.swift            # Banner de alerta offline con reintento
├── Components/
│   ├── StatusBadge.swift
│   ├── IconLabelIndicator.swift
│   ├── GlanceableCard.swift
│   ├── CircularProgressRing.swift
│   ├── StopHeaderView.swift
│   ├── RecipientInfoView.swift
│   ├── AddressCompactView.swift
│   ├── EstimatedArrivalBadge.swift
│   ├── DeliveryButton.swift
│   ├── SecondaryActionButton.swift
│   ├── MiniMapSnapshot.swift
│   ├── RouteProgressBar.swift
│   ├── SwipeActionButton.swift
│   └── BluetoothSyncIndicator.swift
├── Services/
│   ├── WatchConnectivityService.swift       # Protocolo de abstracción
│   ├── MockBluetoothPayloadProvider.swift    # Mock para pruebas y SwiftUI Previews
│   ├── WatchConnectivityManager.swift        # Implementación real con WCSessionDelegate
│   ├── ReachabilityObserver.swift            # Observador de enlace Bluetooth
│   ├── LocalCacheManager.swift               # Almacenamiento y cola offline
│   └── HapticFeedbackManager.swift           # Manejador de respuesta háptica
├── Utilities/
│   └── DesignSystem/
│       ├── ColorTokens.swift                 # Tokens de color Modo Claro calibrados
│       ├── TypographyTokens.swift            # Tipografía glanceable y legible
│       ├── LayoutTokens.swift                # Espaciado y radios de curvatura
│       └── WatchScreenGeometry.swift         # Adaptación Apple Watch Series 9 & Ultra
└── Tests/
    └── DeliveryRouteViewModelTests.swift     # Pruebas unitarias de transiciones y cola offline
```

---

## 4. Requisitos y Ejecución
- **Target Mínimo:** watchOS 9.0+
- **Dispositivos soportados:** Apple Watch 40mm, 41mm, 44mm, 45mm y 49mm (Ultra)
- **Xcode:** 14.0+ / Swift 5.7+
- **SwiftUI Previews:** Cada componente y pantalla cuenta con su macro `#Preview` integrada con datos mock interactivos.
