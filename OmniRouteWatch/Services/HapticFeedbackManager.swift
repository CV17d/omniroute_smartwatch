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
    
    /// Triggers prominent alert haptic pattern when marking a recipient absent
    public func playAbsentAlert() {
        #if os(watchOS)
        WKInterfaceDevice.current().play(.failure)
        #endif
    }
    
    /// Triggers cautionary warning pattern when filing an incident
    public func playIncidentWarning() {
        #if os(watchOS)
        WKInterfaceDevice.current().play(.retry)
        #endif
    }
    
    /// Triggers turn cue haptic when approaching delivery destination
    public func playNavigationPrompt() {
        #if os(watchOS)
        WKInterfaceDevice.current().play(.directionDown)
        #endif
    }
}
