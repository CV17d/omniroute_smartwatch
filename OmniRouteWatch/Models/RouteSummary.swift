import Foundation

// MARK: - Vehicle Type Enum
public enum VehicleType: String, Codable, Sendable {
    case moto = "MOTO"
    case bicycle = "BICICLETA"
    case car = "CARRO"
    case van = "FURGON"
    
    public var iconName: String {
        switch self {
        case .moto: return "bicycle" // or custom moto SF symbol
        case .bicycle: return "figure.outdoor.cycle"
        case .car: return "car.fill"
        case .van: return "box.truck.fill"
        }
    }
}

// MARK: - RouteSummary DTO (Lightweight BLE / WCSession Transfer)
/// Compact payload sent over Bluetooth from the companion phone to watchOS.
/// Stripped of heavy metadata to fit within WCSession transmission constraints.
public struct RouteSummary: Identifiable, Codable, Equatable, Sendable {
    public let id: String
    public let routeName: String
    public let vehicleType: VehicleType
    public let stops: [DeliveryStop]
    public let totalStops: Int
    public let completedStops: Int
    public let totalDistanceKm: Double
    public let estimatedDurationMinutes: Int
    public let waypoints: [GeoCoordinate]
    
    public init(
        id: String = UUID().uuidString,
        routeName: String = "RUTA: MOTO",
        vehicleType: VehicleType = .moto,
        stops: [DeliveryStop] = [],
        totalStops: Int = 15,
        completedStops: Int = 4,
        totalDistanceKm: Double = 12.4,
        estimatedDurationMinutes: Int = 95,
        waypoints: [GeoCoordinate] = []
    ) {
        self.id = id
        self.routeName = routeName
        self.vehicleType = vehicleType
        self.stops = stops
        self.totalStops = totalStops
        self.completedStops = completedStops
        self.totalDistanceKm = totalDistanceKm
        self.estimatedDurationMinutes = estimatedDurationMinutes
        self.waypoints = waypoints
    }
    
    /// Normalized progress ratio 0.0 ... 1.0 for CircularProgressRing
    public var progressRatio: Double {
        guard totalStops > 0 else { return 0.0 }
        return Double(completedStops) / Double(totalStops)
    }
    
    /// Label as shown in design: "4/15 ENTREGAS"
    public var glanceableProgressLabel: String {
        "\(completedStops)/\(totalStops) ENTREGAS"
    }
    
    /// Returns the currently active delivery stop (first pending or inProgress)
    public var activeStop: DeliveryStop? {
        stops.first(where: { $0.status == .inProgress || $0.status == .arrived })
            ?? stops.first(where: { $0.status == .pending })
    }
    
    /// Returns the index of the active stop (0-indexed)
    public var activeStopIndex: Int {
        if let stop = activeStop, let index = stops.firstIndex(where: { $0.id == stop.id }) {
            return index
        }
        return 0
    }
}

// MARK: - Mock RouteSummary for Previews
extension RouteSummary {
    public static let sampleMotoRoute: RouteSummary = {
        let stop1 = DeliveryStop(
            id: "stop-1",
            stopNumber: 1,
            address: "CRA 15 # 85-30",
            recipientName: "Maria Gómez",
            apartmentOrSuite: "Oficina 402",
            packageId: "ENV-101",
            coordinate: GeoCoordinate(latitude: 4.6050, longitude: -74.0840),
            distanceText: "0m",
            navigationInstruction: "Completada",
            turnDirection: .straight,
            status: .delivered
        )
        let stop2 = DeliveryStop(
            id: "stop-2",
            stopNumber: 2,
            address: "CALLE 19 # 4-20",
            recipientName: "Pedro Diaz",
            apartmentOrSuite: "Piso 2",
            packageId: "ENV-102",
            coordinate: GeoCoordinate(latitude: 4.6065, longitude: -74.0830),
            distanceText: "0m",
            navigationInstruction: "Completada",
            turnDirection: .straight,
            status: .delivered
        )
        let stop3 = DeliveryStop(
            id: "stop-3",
            stopNumber: 3,
            address: "CRA 10 # 24-05",
            recipientName: "Farmacia Central",
            apartmentOrSuite: "Local 1",
            packageId: "ENV-103",
            coordinate: GeoCoordinate(latitude: 4.6080, longitude: -74.0820),
            distanceText: "0m",
            navigationInstruction: "Completada",
            turnDirection: .straight,
            status: .delivered
        )
        let stop4 = DeliveryStop(
            id: "stop-4",
            stopNumber: 4,
            address: "CALLE 22 # 5-43",
            recipientName: "Envia",
            apartmentOrSuite: "Apt 302",
            packageId: "ENV-104",
            coordinate: GeoCoordinate(latitude: 4.6097, longitude: -74.0817),
            distanceText: "150m",
            navigationInstruction: "Continuar abajo",
            turnDirection: .down,
            status: .inProgress,
            notes: "Timbre 302",
            estimatedMinutesArrival: 2
        )
        let stop5 = DeliveryStop(
            id: "stop-5",
            stopNumber: 5,
            address: "CRA 7 # 32-16",
            recipientName: "Carlos Mendez",
            apartmentOrSuite: "Apt 501",
            packageId: "ENV-105",
            coordinate: GeoCoordinate(latitude: 4.6150, longitude: -74.0720),
            distanceText: "600m",
            navigationInstruction: "Girar derecha",
            turnDirection: .turnRight,
            status: .pending
        )
        
        let waypoints = [
            GeoCoordinate(latitude: 4.6050, longitude: -74.0840),
            GeoCoordinate(latitude: 4.6065, longitude: -74.0830),
            GeoCoordinate(latitude: 4.6080, longitude: -74.0820),
            GeoCoordinate(latitude: 4.6097, longitude: -74.0817),
            GeoCoordinate(latitude: 4.6150, longitude: -74.0720)
        ]
        
        return RouteSummary(
            id: "route-today-01",
            routeName: "RUTA: MOTO",
            vehicleType: .moto,
            stops: [stop1, stop2, stop3, stop4, stop5],
            totalStops: 15,
            completedStops: 4,
            totalDistanceKm: 14.8,
            estimatedDurationMinutes: 85,
            waypoints: waypoints
        )
    }()
}
