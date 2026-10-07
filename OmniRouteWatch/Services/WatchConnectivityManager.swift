import Foundation
import Combine
import WatchConnectivity

// MARK: - WatchConnectivityManager
/// Production WatchConnectivity manager handling live WCSession delegate events,
/// Bluetooth reachability changes, and bidirectional JSON DTO decoding.
public final class WatchConnectivityManager: NSObject, WatchConnectivityService, WCSessionDelegate, @unchecked Sendable {
    public static let shared = WatchConnectivityManager()
    
    private let routeSubject = PassthroughSubject<RouteSummary, Never>()
    private let stateSubject = CurrentValueSubject<ConnectivityState, Never>(.disconnected)
    
    private let jsonDecoder = JSONDecoder()
    private let jsonEncoder = JSONEncoder()
    
    public var isReachable: Bool {
        guard WCSession.isSupported() else { return false }
        return WCSession.default.isReachable
    }
    
    public var connectivityState: ConnectivityState {
        stateSubject.value
    }
    
    public var routePublisher: AnyPublisher<RouteSummary, Never> {
        routeSubject.eraseToAnyPublisher()
    }
    
    public var statePublisher: AnyPublisher<ConnectivityState, Never> {
        stateSubject.eraseToAnyPublisher()
    }
    
    public override init() {
        super.init()
    }
    
    public func activate() {
        guard WCSession.isSupported() else {
            stateSubject.send(.error)
            return
        }
        let session = WCSession.default
        session.delegate = self
        stateSubject.send(.connecting)
        session.activate()
    }
    
    public func sendStatusUpdate(_ event: DeliveryStatusUpdateEvent) async throws {
        guard WCSession.isSupported() else {
            throw NSError(domain: "omniroute.wcsession", code: -1, userInfo: [NSLocalizedDescriptionKey: "WCSession not supported"])
        }
        let session = WCSession.default
        let data = try jsonEncoder.encode(event)
        let payload: [String: Any] = [
            "type": "delivery_status_update",
            "payload": data
        ]
        
        if session.isReachable {
            // Immediate real-time transmission over Bluetooth
            try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
                session.sendMessage(payload, replyHandler: { _ in
                    continuation.resume()
                }, errorHandler: { error in
                    continuation.resume(throwing: error)
                })
            }
        } else {
            // Guaranteed background queueing via transferUserInfo
            session.transferUserInfo(payload)
        }
    }
    
    public func requestRouteSync() async throws {
        guard WCSession.isSupported() else { return }
        let session = WCSession.default
        let payload: [String: Any] = ["type": "request_route_sync"]
        
        if session.isReachable {
            session.sendMessage(payload, replyHandler: { [weak self] reply in
                self?.handleIncomingDictionary(reply)
            }, errorHandler: { error in
                print("Failed to sync route over WCSession: \(error.localizedDescription)")
            })
        }
    }
    
    // MARK: - Internal Message Handling
    private func handleIncomingDictionary(_ dict: [String: Any]) {
        guard let type = dict["type"] as? String else { return }
        if type == "route_summary", let data = dict["payload"] as? Data {
            do {
                let route = try jsonDecoder.decode(RouteSummary.self, from: data)
                routeSubject.send(route)
            } catch {
                print("Failed to decode RouteSummary: \(error)")
            }
        }
    }
    
    // MARK: - WCSessionDelegate
    public func session(
        _ session: WCSession,
        activationDidCompleteWith activationState: WCSessionActivationState,
        error: Error?
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            if let error = error {
                print("WCSession activation error: \(error.localizedDescription)")
                self.stateSubject.send(.error)
                return
            }
            switch activationState {
            case .activated:
                self.stateSubject.send(session.isReachable ? .connected : .disconnected)
            case .inactive, .notActivated:
                self.stateSubject.send(.disconnected)
            @unknown default:
                self.stateSubject.send(.disconnected)
            }
        }
    }
    
    public func sessionReachabilityDidChange(_ session: WCSession) {
        DispatchQueue.main.async { [weak self] in
            self?.stateSubject.send(session.isReachable ? .connected : .disconnected)
        }
    }
    
    public func session(_ session: WCSession, didReceiveMessage message: [String : Any]) {
        DispatchQueue.main.async { [weak self] in
            self?.handleIncomingDictionary(message)
        }
    }
    
    public func session(_ session: WCSession, didReceiveUserInfo userInfo: [String : Any] = [:]) {
        DispatchQueue.main.async { [weak self] in
            self?.handleIncomingDictionary(userInfo)
        }
    }
    
    public func session(_ session: WCSession, didReceiveApplicationContext applicationContext: [String : Any]) {
        DispatchQueue.main.async { [weak self] in
            self?.handleIncomingDictionary(applicationContext)
        }
    }
}
