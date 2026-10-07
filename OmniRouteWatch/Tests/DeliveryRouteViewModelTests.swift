import XCTest
import Combine
@testable import OmniRouteWatch

// MARK: - DeliveryRouteViewModelTests
final class DeliveryRouteViewModelTests: XCTestCase {
    private var mockConnectivity: MockBluetoothPayloadProvider!
    private var mockCache: LocalCacheManager!
    private var viewModel: DeliveryRouteViewModel!
    
    @MainActor
    override func setUp() {
        super.setUp()
        mockConnectivity = MockBluetoothPayloadProvider()
        mockCache = LocalCacheManager(userDefaults: UserDefaults(suiteName: "test_omniroute_suite") ?? .standard)
        mockCache.clearCache()
        
        viewModel = DeliveryRouteViewModel(
            connectivityService: mockConnectivity,
            cacheManager: mockCache,
            initialRoute: RouteSummary.sampleMotoRoute
        )
    }
    
    @MainActor
    override func tearDown() {
        viewModel = nil
        mockConnectivity = nil
        mockCache.clearCache()
        mockCache = nil
        super.tearDown()
    }
    
    // MARK: - Initialization Tests
    @MainActor
    func testInitializationState() {
        XCTAssertNotNil(viewModel.route)
        XCTAssertEqual(viewModel.route.routeName, "RUTA: MOTO")
        XCTAssertEqual(viewModel.route.stops.count, 5)
        XCTAssertEqual(viewModel.currentStopIndex, 3) // Stop 4 ("CALLE 22 # 5-43") is active
        XCTAssertEqual(viewModel.currentStop?.address, "CALLE 22 # 5-43")
    }
    
    // MARK: - Arrive at Stop
    @MainActor
    func testArriveAtStop() {
        viewModel.arriveAtStop()
        
        XCTAssertTrue(viewModel.showConfirmationModal)
        XCTAssertEqual(viewModel.currentStop?.status, .arrived)
    }
    
    // MARK: - Confirm Delivery
    @MainActor
    func testConfirmDeliveryStateTransition() async throws {
        let initialCompleted = viewModel.route.completedStops
        let stopId = viewModel.currentStop?.id
        
        viewModel.confirmDelivery()
        
        // Stop status updated
        let matchingStop = viewModel.route.stops.first(where: { $0.id == stopId })
        XCTAssertEqual(matchingStop?.status, .delivered)
        
        // Wait for async dispatch
        try await Task.sleep(nanoseconds: 100_000_000)
        
        // Verify event dispatched
        let dispatched = mockConnectivity.dispatchedEvents.first(where: { $0.stopId == stopId })
        XCTAssertNotNil(dispatched)
        XCTAssertEqual(dispatched?.newStatus, .delivered)
        XCTAssertEqual(viewModel.route.completedStops, initialCompleted + 1)
    }
    
    // MARK: - Mark as Absent
    @MainActor
    func testMarkAsAbsentStateTransition() async throws {
        let stopId = viewModel.currentStop?.id
        
        viewModel.markAsAbsent(reason: "Nadie abre la puerta")
        
        let matchingStop = viewModel.route.stops.first(where: { $0.id == stopId })
        XCTAssertEqual(matchingStop?.status, .absent)
        
        try await Task.sleep(nanoseconds: 100_000_000)
        
        let dispatched = mockConnectivity.dispatchedEvents.first(where: { $0.stopId == stopId })
        XCTAssertNotNil(dispatched)
        XCTAssertEqual(dispatched?.newStatus, .absent)
        XCTAssertEqual(dispatched?.notes, "Nadie abre la puerta")
    }
    
    // MARK: - Offline Queueing
    @MainActor
    func testOfflineQueueingWhenDisconnected() async throws {
        mockConnectivity.simulateConnectionState(.disconnected)
        
        let stopId = viewModel.currentStop?.id ?? "stop-4"
        viewModel.dispatchStatusUpdate(stopId: stopId, status: .delivered, notes: "Offline test")
        
        try await Task.sleep(nanoseconds: 100_000_000)
        
        // Event should be safely stored in offline cache
        let pending = mockCache.pendingEvents()
        XCTAssertFalse(pending.isEmpty)
        XCTAssertEqual(pending.first?.stopId, stopId)
    }
    
    // MARK: - Route Completion
    @MainActor
    func testRouteCompletionAfterLastStop() {
        for stop in viewModel.route.stops {
            viewModel.updateStopStatus(stopId: stop.id, newStatus: .delivered)
        }
        viewModel.advanceToNextStop()
        
        XCTAssertTrue(viewModel.isRouteCompleted)
        XCTAssertFalse(viewModel.isDeliveringActive)
    }
}
