import Foundation

// MARK: - LocalCacheManagerProtocol
public protocol LocalCacheManaging: AnyObject, Sendable {
    func saveRoute(_ route: RouteSummary)
    func loadCachedRoute() -> RouteSummary?
    func queuePendingEvent(_ event: DeliveryStatusUpdateEvent)
    func pendingEvents() -> [DeliveryStatusUpdateEvent]
    func clearPendingEvents()
    func clearCache()
}

// MARK: - LocalCacheManager Implementation
/// Persistent cache maintaining the active route and an offline queue of events
/// to guarantee zero data loss when Bluetooth connection drops during delivery.
public final class LocalCacheManager: LocalCacheManaging, @unchecked Sendable {
    public static let shared = LocalCacheManager()
    
    private let userDefaults: UserDefaults
    private let queueKey = "omniroute.offline_events_queue"
    private let routeKey = "omniroute.cached_route_summary"
    private let lock = NSLock()
    
    private let encoder = JSONEncoder()
    private let decoder = JSONDecoder()
    
    public init(userDefaults: UserDefaults = .standard) {
        self.userDefaults = userDefaults
    }
    
    public func saveRoute(_ route: RouteSummary) {
        lock.lock()
        defer { lock.unlock() }
        if let data = try? encoder.encode(route) {
            userDefaults.set(data, forKey: routeKey)
        }
    }
    
    public func loadCachedRoute() -> RouteSummary? {
        lock.lock()
        defer { lock.unlock() }
        guard let data = userDefaults.data(forKey: routeKey) else { return nil }
        return try? decoder.decode(RouteSummary.self, from: data)
    }
    
    public func queuePendingEvent(_ event: DeliveryStatusUpdateEvent) {
        lock.lock()
        defer { lock.unlock() }
        var current = pendingEventsInternal()
        current.append(event)
        if let data = try? encoder.encode(current) {
            userDefaults.set(data, forKey: queueKey)
        }
    }
    
    public func pendingEvents() -> [DeliveryStatusUpdateEvent] {
        lock.lock()
        defer { lock.unlock() }
        return pendingEventsInternal()
    }
    
    private func pendingEventsInternal() -> [DeliveryStatusUpdateEvent] {
        guard let data = userDefaults.data(forKey: queueKey) else { return [] }
        return (try? decoder.decode([DeliveryStatusUpdateEvent].self, from: data)) ?? []
    }
    
    public func clearPendingEvents() {
        lock.lock()
        defer { lock.unlock() }
        userDefaults.removeObject(forKey: queueKey)
    }
    
    public func clearCache() {
        lock.lock()
        defer { lock.unlock() }
        userDefaults.removeObject(forKey: routeKey)
        userDefaults.removeObject(forKey: queueKey)
    }
}
