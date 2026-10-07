import Foundation
import CoreLocation

// MARK: - Navigation Turn Direction
public enum NavigationDirection: String, Codable, Sendable {
    case down = "down"
    case straight = "straight"
    case turnLeft = "turn_left"
    case turnRight = "turn_right"
    case slightLeft = "slight_left"
    case slightRight = "slight_right"
    case uTurn = "u_turn"
    
    public var systemIcon: String {
        switch self {
        case .down: return "arrow.down"
        case .straight: return "arrow.up"
        case .turnLeft: return "arrow.turn.up.left"
        case .turnRight: return "arrow.turn.up.right"
        case .slightLeft: return "arrow.up.left"
        case .slightRight: return "arrow.up.right"
        case .uTurn: return "arrow.uturn.down"
        }
    }
}

// MARK: - Coordinate Structure
public struct GeoCoordinate: Codable, Equatable, Hashable, Sendable {
    public let latitude: Double
    public let longitude: Double
    
    public init(latitude: Double, longitude: Double) {
        self.latitude = latitude
        self.longitude = longitude
    }
    
    public var clLocationCoordinate2D: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

// MARK: - DeliveryStop Domain Model
/// A single delivery destination received via Bluetooth from companion mobile app.
public struct DeliveryStop: Identifiable, Codable, Equatable, Hashable, Sendable {
    public let id: String
    public let stopNumber: Int
    public let address: String
    public let recipientName: String
    public let apartmentOrSuite: String?
    public let packageId: String
    public let coordinate: GeoCoordinate
    public let distanceText: String
    public let navigationInstruction: String
    public let turnDirection: NavigationDirection
    public var status: DeliveryStatus
    public let notes: String?
    public let estimatedMinutesArrival: Int
    
    public init(
        id: String = UUID().uuidString,
        stopNumber: Int,
        address: String,
        recipientName: String,
        apartmentOrSuite: String? = nil,
        packageId: String,
        coordinate: GeoCoordinate,
        distanceText: String,
        navigationInstruction: String,
        turnDirection: NavigationDirection = .down,
        status: DeliveryStatus = .pending,
        notes: String? = nil,
        estimatedMinutesArrival: Int = 3
    ) {
        self.id = id
        self.stopNumber = stopNumber
        self.address = address
        self.recipientName = recipientName
        self.apartmentOrSuite = apartmentOrSuite
        self.packageId = packageId
        self.coordinate = coordinate
        self.distanceText = distanceText
        self.navigationInstruction = navigationInstruction
        self.turnDirection = turnDirection
        self.status = status
        self.notes = notes
        self.estimatedMinutesArrival = estimatedMinutesArrival
    }
    
    /// Glanceable formatted subtitle (e.g. "Envia • Apt 302")
    public var glanceableSubtitle: String {
        if let apt = apartmentOrSuite, !apt.isEmpty {
            return "\(recipientName) • \(apt)"
        }
        return recipientName
    }
}

// MARK: - Mock Sample Deliveries
extension DeliveryStop {
    public static let sampleCalle22 = DeliveryStop(
        id: "stop-sample-1",
        stopNumber: 4,
        address: "CALLE 22 # 5-43",
        recipientName: "Envia",
        apartmentOrSuite: "Apt 302",
        packageId: "ENV-2024-991",
        coordinate: GeoCoordinate(latitude: 4.6097, longitude: -74.0817),
        distanceText: "150m",
        navigationInstruction: "Continuar abajo",
        turnDirection: .down,
        status: .inProgress,
        notes: "Timbre 302, dejar en recepción si no contesta",
        estimatedMinutesArrival: 2
    )
    
    public static let sampleCarrera7 = DeliveryStop(
        id: "stop-sample-2",
        stopNumber: 5,
        address: "CRA 7 # 32-16",
        recipientName: "Carlos Mendez",
        apartmentOrSuite: "Oficina 501",
        packageId: "ENV-2024-992",
        coordinate: GeoCoordinate(latitude: 4.6150, longitude: -74.0720),
        distanceText: "600m",
        navigationInstruction: "Girar a la derecha",
        turnDirection: .turnRight,
        status: .pending,
        notes: "Portería principal",
        estimatedMinutesArrival: 6
    )
}
