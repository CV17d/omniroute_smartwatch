import Foundation
import SwiftUI

// MARK: - Delivery Actions Extension
extension DeliveryRouteViewModel {
    /// Confirms successful package dropoff ("✓ ENTREGADO", Watch 3)
    public func confirmDelivery() {
        guard let stop = currentStop else { return }
        
        // 1. Mutate local stop state
        updateStopStatus(stopId: stop.id, newStatus: .delivered)
        
        // 2. Dispatch Bluetooth event or queue offline
        dispatchStatusUpdate(stopId: stop.id, status: .delivered, notes: "Entregado por mensajero")
        
        // 3. Trigger haptic feedback for success
        HapticFeedbackManager.shared.playDeliverySuccess()
        
        // 4. Auto-advance to next delivery stop after brief glanceable confirmation
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) { [weak self] in
            guard let self = self else { return }
            self.showConfirmationModal = false
            self.advanceToNextStop()
        }
    }
}
