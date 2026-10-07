import Foundation

// MARK: - Navigation & Stop Index Extensions
extension DeliveryRouteViewModel {
    /// Returns the currently active delivery stop based on index
    public var currentStop: DeliveryStop? {
        guard currentStopIndex >= 0 && currentStopIndex < route.stops.count else {
            return route.activeStop
        }
        return route.stops[currentStopIndex]
    }
    
    /// Stops remaining to be delivered
    public var remainingStops: [DeliveryStop] {
        route.stops.filter { !$0.status.isFinalState }
    }
    
    /// Starts route navigation (action from Watch 1 "INICIAR RUTA")
    public func startRoute() {
        isRouteStarted = true
        isDeliveringActive = true
        if let active = route.activeStop,
           let index = route.stops.firstIndex(where: { $0.id == active.id }) {
            currentStopIndex = index
        }
    }
    
    /// Action triggered when courier taps "¡LLEGUE!" (Watch 2)
    public func arriveAtStop() {
        guard let stop = currentStop else { return }
        updateStopStatus(stopId: stop.id, newStatus: .arrived)
        showConfirmationModal = true
    }
    
    /// Advances index to next pending stop or completes route if none remain
    public func advanceToNextStop() {
        let pending = route.stops.indices.filter { !route.stops[$0].status.isFinalState }
        if let nextPending = pending.first(where: { $0 > currentStopIndex }) {
            currentStopIndex = nextPending
        } else if let firstRemaining = pending.first {
            currentStopIndex = firstRemaining
        } else {
            // All deliveries are completed
            isRouteCompleted = true
            isDeliveringActive = false
        }
    }
    
    /// Selects a specific stop by identifier from the overview list
    public func selectStop(id: String) {
        if let index = route.stops.firstIndex(where: { $0.id == id }) {
            currentStopIndex = index
            showRouteOverview = false
        }
    }
    
    /// Resets route progress for testing/simulation
    public func resetRouteForDemo() {
        route = RouteSummary.sampleMotoRoute
        cacheManager.saveRoute(route)
        currentStopIndex = 3
        isRouteStarted = true
        isDeliveringActive = true
        isRouteCompleted = false
        showConfirmationModal = false
    }
}
