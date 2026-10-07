import Foundation
import Combine

// MARK: - Connection Status Enum
public enum ConnectivityState: String, Sendable {
    case disconnected
    case connecting
    case connected
    case error
    
    public var glanceableLabel: String {
        switch self {
        case .disconnected: return "Sin Conexión"
        case .connecting: return "Conectando..."
        case .connected: return "En Línea"
        case .error: return "Error Sync"
        }
    }
}

// MARK: - WatchConnectivityService Protocol
/// Protocol abstracting Bluetooth and WCSession bidirectional communication
/// between watchOS and the iOS companion application.
public protocol WatchConnectivityService: AnyObject, Sendable {
    /// Indicates whether the companion iPhone is reachable via Bluetooth
    var isReachable: Bool { get }
    
    /// Current Bluetooth connectivity state
    var connectivityState: ConnectivityState { get }
    
    /// Stream of incoming RouteSummary updates received from companion phone
    var routePublisher: AnyPublisher<RouteSummary, Never> { get }
    
    /// Stream of connectivity state updates
    var statePublisher: AnyPublisher<ConnectivityState, Never> { get }
    
    /// Transmits a delivery status change event ("Entregado", "Ausente", "Incidencia") to iPhone
    func sendStatusUpdate(_ event: DeliveryStatusUpdateEvent) async throws
    
    /// Sends a request to the iPhone companion app to synchronize the latest route
    func requestRouteSync() async throws
    
    /// Activates the connectivity session
    func activate()
}
