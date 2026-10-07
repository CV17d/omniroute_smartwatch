import Foundation

// MARK: - Bluetooth Synchronization Extension
extension DeliveryRouteViewModel {
    /// Mutates the local stop status and recalculates route summary totals
    public func updateStopStatus(stopId: String, newStatus: DeliveryStatus) {
        var stops = route.stops
        guard let index = stops.firstIndex(where: { $0.id == stopId }) else { return }
        
        var updated = stops[index]
        updated.status = newStatus
        stops[index] = updated
        
        let completed = stops.filter { $0.status.isFinalState }.count
        let updatedRoute = RouteSummary(
            id: route.id,
            routeName: route.routeName,
            vehicleType: route.vehicleType,
            stops: stops,
            totalStops: route.totalStops,
            completedStops: completed,
            totalDistanceKm: route.totalDistanceKm,
            estimatedDurationMinutes: route.estimatedDurationMinutes,
            waypoints: route.waypoints
        )
        
        self.route = updatedRoute
        self.cacheManager.saveRoute(updatedRoute)
    }
    
    /// Dispatches delivery status mutations over Bluetooth to companion app
    public func dispatchStatusUpdate(
        stopId: String,
        status: DeliveryStatus,
        notes: String?
    ) {
        let event = DeliveryStatusUpdateEvent(
            stopId: stopId,
            newStatus: status,
            timestamp: Date(),
            notes: notes
        )
        
        Task { [weak self] in
            guard let self = self else { return }
            do {
                if self.connectivityService.isReachable {
                    try await self.connectivityService.sendStatusUpdate(event)
                } else {
                    // Queue offline when phone is disconnected
                    self.cacheManager.queuePendingEvent(event)
                }
            } catch {
                // On communication failure, safely queue offline
                self.cacheManager.queuePendingEvent(event)
                self.lastErrorMessage = "Guardado offline. Se sincronizará al conectar."
            }
        }
    }
}
