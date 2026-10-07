import Foundation
import Combine

// MARK: - ReachabilityObserver
/// High-level observable object monitoring companion phone Bluetooth link
/// and emitting updates for SwiftUI views, status banners, and warning alerts.
@MainActor
public final class ReachabilityObserver: ObservableObject {
    @Published public private(set) var isConnected: Bool = true
    @Published public private(set) var state: ConnectivityState = .connected
    @Published public private(set) var lastSyncDate: Date? = Date()
    @Published public private(set) var hasConnectionWarning: Bool = false
    
    private let connectivityService: WatchConnectivityService
    private var cancellables = Set<AnyCancellable>()
    
    public init(connectivityService: WatchConnectivityService) {
        self.connectivityService = connectivityService
        self.isConnected = connectivityService.isReachable
        self.state = connectivityService.connectivityState
        
        setupSubscriptions()
    }
    
    private func setupSubscriptions() {
        connectivityService.statePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] newState in
                guard let self = self else { return }
                self.state = newState
                let connected = (newState == .connected)
                self.isConnected = connected
                self.hasConnectionWarning = !connected
                if connected {
                    self.lastSyncDate = Date()
                }
            }
            .store(in: &cancellables)
    }
    
    /// Requests a reconnect / retry ping to companion phone
    public func retryConnection() {
        Task {
            do {
                try await connectivityService.requestRouteSync()
            } catch {
                print("Retry connection ping failed: \(error)")
            }
        }
    }
}
