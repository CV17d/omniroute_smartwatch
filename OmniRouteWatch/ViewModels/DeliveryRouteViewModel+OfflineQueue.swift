import Foundation

// MARK: - Offline Queue Management Extension
extension DeliveryRouteViewModel {
    /// Attempts to flush and transmit all pending offline events stored in cache
    public func flushPendingOfflineEvents() {
        guard connectivityService.isReachable else { return }
        let pending = cacheManager.pendingEvents()
        guard !pending.isEmpty else { return }
        
        Task { [weak self] in
            guard let self = self else { return }
            var failedEvents: [DeliveryStatusUpdateEvent] = []
            
            for event in pending {
                do {
                    try await self.connectivityService.sendStatusUpdate(event)
                } catch {
                    failedEvents.append(event)
                }
            }
            
            self.cacheManager.clearPendingEvents()
            for failed in failedEvents {
                self.cacheManager.queuePendingEvent(failed)
            }
        }
    }
    
    /// Requests a fresh route synchronization from the companion app
    public func requestSync() {
        Task { [weak self] in
            guard let self = self else { return }
            self.isLoading = true
            do {
                try await self.connectivityService.requestRouteSync()
                self.flushPendingOfflineEvents()
            } catch {
                self.lastErrorMessage = "Error al sincronizar con el teléfono"
            }
            self.isLoading = false
        }
    }
}
