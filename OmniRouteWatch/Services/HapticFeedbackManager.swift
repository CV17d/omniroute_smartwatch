import Foundation
import WatchKit

// MARK: - HapticFeedbackManager
/// Manages tactile haptic patterns for Apple Watch to deliver instantaneous
/// non-visual confirmations to couriers riding on motorcycles.
public final class HapticFeedbackManager: @unchecked Sendable {
    public static let shared = HapticFeedbackManager()
    
    private init() {}
    
    /// Triggers celebratory tactile success pattern upon confirming delivery
    public func playDeliverySuccess() {
        #if os(watchOS)
        WKInterfaceDevice.current().play(.success)
        #endif
    }
}
