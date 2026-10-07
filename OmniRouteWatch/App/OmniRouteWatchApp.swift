import SwiftUI

// MARK: - OmniRouteWatchApp Main Entry Point
@main
public struct OmniRouteWatchApp: App {
    @StateObject private var viewModel: DeliveryRouteViewModel
    @StateObject private var reachability: ReachabilityObserver
    
    public init() {
        // Initialize WatchConnectivity manager
        let connectivity = WatchConnectivityManager.shared
        connectivity.activate()
        
        let vm = DeliveryRouteViewModel(
            connectivityService: connectivity,
            cacheManager: LocalCacheManager.shared,
            initialRoute: RouteSummary.sampleMotoRoute
        )
        
        _viewModel = StateObject(wrappedValue: vm)
        _reachability = StateObject(wrappedValue: ReachabilityObserver(connectivityService: connectivity))
    }
    
    public var body: some Scene {
        WindowGroup {
            OmniRouteRootCoordinatorView(viewModel: viewModel, reachability: reachability)
        }
    }
}

// MARK: - OmniRouteRootCoordinatorView
public struct OmniRouteRootCoordinatorView: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    @ObservedObject public var reachability: ReachabilityObserver
    
    public var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                // Main Flow Switcher matching Reference Images
                if !viewModel.isRouteStarted {
                    // Watch 1: Initial Route Overview & Start
                    RouteOverviewStartView(viewModel: viewModel)
                } else if viewModel.isRouteCompleted {
                    // Completion Summary
                    RouteFinishedSummaryView(viewModel: viewModel)
                } else {
                    // Watch 2: Active Navigation & Stop Delivery
                    ActiveDeliveryView(viewModel: viewModel)
                }
                
                // Connection Lost Banner
                if reachability.hasConnectionWarning {
                    ConnectionLostBanner {
                        reachability.retryConnection()
                    }
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .padding(.top, 4)
                }
            }
            .animation(.easeInOut(duration: 0.25), value: viewModel.isRouteStarted)
            .animation(.easeInOut(duration: 0.25), value: viewModel.isRouteCompleted)
            .animation(.easeInOut(duration: 0.25), value: reachability.hasConnectionWarning)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    BluetoothSyncIndicator(state: reachability.state)
                }
                
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {
                        viewModel.showRouteOverview.toggle()
                    }) {
                        Image(systemName: "list.bullet")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(OmniColors.textPrimary)
                    }
                }
            }
            .sheet(isPresented: $viewModel.showRouteOverview) {
                RouteOverviewListView(viewModel: viewModel)
            }
        }
    }
}

// MARK: - OmniRouteRootCoordinatorView Previews
#Preview("OmniRoute Root Coordinator Preview") {
    let mock = MockBluetoothPayloadProvider()
    let vm = DeliveryRouteViewModel(connectivityService: mock)
    let reachability = ReachabilityObserver(connectivityService: mock)
    return OmniRouteRootCoordinatorView(viewModel: vm, reachability: reachability)
}
