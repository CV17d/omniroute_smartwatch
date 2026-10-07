import Foundation
import Combine
import SwiftUI

// MARK: - DeliveryRouteViewModel
/// Central presentation logic and state coordinator for OmniRoute watchOS.
/// Manages route life cycle, stops progress, navigation indexes, and Bluetooth sync.
@MainActor
public final class DeliveryRouteViewModel: ObservableObject {
    // MARK: - Published States
    @Published public private(set) var route: RouteSummary
    @Published public var currentStopIndex: Int = 0
    @Published public var isRouteStarted: Bool = false
    @Published public var isDeliveringActive: Bool = false
    @Published public var showConfirmationModal: Bool = false
    @Published public var showIncidentSheet: Bool = false
    @Published public var showRouteOverview: Bool = false
    @Published public var isRouteCompleted: Bool = false
    @Published public var isLoading: Bool = false
    @Published public var lastErrorMessage: String? = nil
    
    // MARK: - Dependencies
    public let connectivityService: WatchConnectivityService
    public let cacheManager: LocalCacheManaging
    public var cancellables = Set<AnyCancellable>()
    
    // MARK: - Initialization
    public init(
        connectivityService: WatchConnectivityService = MockBluetoothPayloadProvider(),
        cacheManager: LocalCacheManaging = LocalCacheManager.shared,
        initialRoute: RouteSummary = RouteSummary.sampleMotoRoute
    ) {
        self.connectivityService = connectivityService
        self.cacheManager = cacheManager
        
        // Restore from cache if available, otherwise use initial payload
        if let cached = cacheManager.loadCachedRoute() {
            self.route = cached
        } else {
            self.route = initialRoute
            cacheManager.saveRoute(initialRoute)
        }
        
        self.currentStopIndex = self.route.activeStopIndex
        self.isRouteStarted = self.route.completedStops > 0
        
        setupSubscriptions()
    }
    
    private func setupSubscriptions() {
        // Observe incoming route payloads from iPhone via Bluetooth
        connectivityService.routePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] updatedRoute in
                guard let self = self else { return }
                self.route = updatedRoute
                self.cacheManager.saveRoute(updatedRoute)
                if self.currentStopIndex >= updatedRoute.stops.count {
                    self.currentStopIndex = max(0, updatedRoute.stops.count - 1)
                }
            }
            .store(in: &cancellables)
    }
}
