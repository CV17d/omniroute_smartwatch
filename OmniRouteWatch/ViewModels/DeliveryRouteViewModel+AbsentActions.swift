import Foundation
import SwiftUI

// MARK: - Absent & Exception Actions Extension
extension DeliveryRouteViewModel {
    /// Marks package delivery as absent ("✗ NO ENTREGADO / AUSENTE", Watch 3)
    public func markAsAbsent(reason: String = "Destinatario ausente") {
        guard let stop = currentStop else { return }
        
        // 1. Mutate local stop state
        updateStopStatus(stopId: stop.id, newStatus: .absent)
        
        // 2. Dispatch Bluetooth event or queue offline
        dispatchStatusUpdate(stopId: stop.id, status: .absent, notes: reason)
        
        // 3. Trigger haptic feedback for alert
        HapticFeedbackManager.shared.playAbsentAlert()
        
        // 4. Dismiss modal and advance stop
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) { [weak self] in
            guard let self = self else { return }
            self.showConfirmationModal = false
            self.advanceToNextStop()
        }
    }
    
    /// Reports an incident on the current stop (e.g. wrong address, access denied)
    public func reportIncident(tag: String) {
        guard let stop = currentStop else { return }
        
        updateStopStatus(stopId: stop.id, newStatus: .incident)
        dispatchStatusUpdate(stopId: stop.id, status: .incident, notes: tag)
        HapticFeedbackManager.shared.playIncidentWarning()
        
        showIncidentSheet = false
        advanceToNextStop()
    }
}
