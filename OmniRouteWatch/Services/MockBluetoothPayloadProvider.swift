import Foundation
import Combine

// MARK: - MockBluetoothPayloadProvider
/// Simulated Bluetooth payload provider allowing SwiftUI previews and unit tests
/// to run smoothly without requiring a paired physical iPhone.
public final class MockBluetoothPayloadProvider: WatchConnectivityService, @unchecked Sendable {
    private let routeSubject: CurrentValueSubject<RouteSummary, Never>
    private let stateSubject: CurrentValueSubject<ConnectivityState, Never>
    
    // In-memory log of dispatched events for unit testing verification
    public private(set) var dispatchedEvents: [DeliveryStatusUpdateEvent] = []
    public var shouldSimulateError: Bool = false
    
    public var isReachable: Bool {
        stateSubject.value == .connected
    }
    
    public var connectivityState: ConnectivityState {
        stateSubject.value
    }
    
    public var routePublisher: AnyPublisher<RouteSummary, Never> {
        routeSubject.eraseToAnyPublisher()
    }
    
    public var statePublisher: AnyPublisher<ConnectivityState, Never> {
        stateSubject.eraseToAnyPublisher()
    }
    
    public init(
        initialRoute: RouteSummary = RouteSummary.sampleMotoRoute,
        initialState: ConnectivityState = .connected
    ) {
        self.routeSubject = CurrentValueSubject(initialRoute)
        self.stateSubject = CurrentValueSubject(initialState)
    }
    
    public func activate() {
        // Mock provider activates immediately
        if stateSubject.value == .disconnected {
            stateSubject.send(.connected)
        }
    }
    
    public func sendStatusUpdate(_ event: DeliveryStatusUpdateEvent) async throws {
        if shouldSimulateError {
            throw NSError(
                domain: "omniroute.watch.error",
                code: 1001,
                userInfo: [NSLocalizedDescriptionKey: "Bluetooth simulated transmission failure"]
            )
        }
        dispatchedEvents.append(event)
        
        // Mutate local mock route to reflect status
        var currentRoute = routeSubject.value
        var updatedStops = currentRoute.stops
        if let index = updatedStops.firstIndex(where: { $0.id == event.stopId }) {
            var updated = updatedStops[index]
            updated.status = event.newStatus
            updatedStops[index] = updated
            
            let completedCount = updatedStops.filter { $0.status.isFinalState }.count
            let newRoute = RouteSummary(
                id: currentRoute.id,
                routeName: currentRoute.routeName,
                vehicleType: currentRoute.vehicleType,
                stops: updatedStops,
                totalStops: currentRoute.totalStops,
                completedStops: completedCount,
                totalDistanceKm: currentRoute.totalDistanceKm,
                estimatedDurationMinutes: currentRoute.estimatedDurationMinutes,
                waypoints: currentRoute.waypoints
            )
            routeSubject.send(newRoute)
        }
    }
    
    public func requestRouteSync() async throws {
        if shouldSimulateError {
            throw NSError(domain: "omniroute.watch.error", code: 1002, userInfo: nil)
        }
        // Re-emit current or refreshed route
        routeSubject.send(routeSubject.value)
    }
    
    // MARK: - Test & Simulation Controls
    public func simulateConnectionState(_ state: ConnectivityState) {
        stateSubject.send(state)
    }
    
    public func simulateRoutePayload(_ route: RouteSummary) {
        routeSubject.send(route)
    }
}
