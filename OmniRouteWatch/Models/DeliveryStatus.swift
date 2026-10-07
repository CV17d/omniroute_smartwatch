import SwiftUI

// MARK: - DeliveryStatus Enum
/// Represents the life-cycle states of a delivery stop on the watchOS client.
public enum DeliveryStatus: String, Codable, CaseIterable, Sendable {
    case pending = "pending"
    case inProgress = "in_progress"
    case arrived = "arrived"
    case delivered = "delivered"
    case absent = "absent"
    case incident = "incident"
    
    // MARK: - Presentation Properties
    public var displayName: String {
        switch self {
        case .pending: return "PENDIENTE"
        case .inProgress: return "EN RUTA"
        case .arrived: return "EN DESTINO"
        case .delivered: return "ENTREGADO"
        case .absent: return "AUSENTE"
        case .incident: return "INCIDENCIA"
        }
    }
    
    public var systemIcon: String {
        switch self {
        case .pending: return "clock"
        case .inProgress: return "figure.walk"
        case .arrived: return "mappin.and.ellipse"
        case .delivered: return "checkmark.circle.fill"
        case .absent: return "xmark.circle.fill"
        case .incident: return "exclamationmark.triangle.fill"
        }
    }
    
    public var statusColor: Color {
        switch self {
        case .pending: return OmniColors.textSecondary
        case .inProgress: return OmniColors.deliveryGreenDark
        case .arrived: return OmniColors.deliveryGreen
        case .delivered: return OmniColors.deliveryGreen
        case .absent: return OmniColors.alertRed
        case .incident: return OmniColors.warningAmber
        }
    }
    
    public var isFinalState: Bool {
        switch self {
        case .delivered, .absent, .incident:
            return true
        case .pending, .inProgress, .arrived:
            return false
        }
    }
    
    // MARK: - State Transition Logic
    public func canTransition(to next: DeliveryStatus) -> Bool {
        switch self {
        case .pending:
            return next == .inProgress || next == .arrived || next == .incident
        case .inProgress:
            return next == .arrived || next == .delivered || next == .absent || next == .incident
        case .arrived:
            return next == .delivered || next == .absent || next == .incident
        case .delivered, .absent, .incident:
            // Final states cannot normally transition unless re-opened by companion app
            return false
        }
    }
}

// MARK: - DeliveryStatusEvent DTO
/// Payload dispatched back to companion phone app upon state mutation.
public struct DeliveryStatusUpdateEvent: Codable, Equatable, Sendable {
    public let stopId: String
    public let newStatus: DeliveryStatus
    public let timestamp: Date
    public let notes: String?
    
    public init(
        stopId: String,
        newStatus: DeliveryStatus,
        timestamp: Date = Date(),
        notes: String? = nil
    ) {
        self.stopId = stopId
        self.newStatus = newStatus
        self.timestamp = timestamp
        self.notes = notes
    }
}
