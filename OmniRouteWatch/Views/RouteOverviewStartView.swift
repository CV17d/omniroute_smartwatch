import SwiftUI

// MARK: - RouteOverviewStartView (Watch 1)
/// Faithfully clones the initial route overview screen displayed on Watch 1:
/// - Header: "OMNIROUTE"
/// - Subtitle tag: "RUTA: MOTO 🏍"
/// - Center: Vector route mini map with circuit polyline and courier pin
/// - Bottom chip: "4/15 ENTREGAS ◯" with circular progress ring
/// - Primary action: "INICIAR RUTA" (Massive delivery green pill button)
public struct RouteOverviewStartView: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        ZStack {
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            VStack(spacing: OmniLayout.spacingSmall) {
                // MARK: - Header: OMNIROUTE & RUTA: MOTO
                VStack(spacing: 1) {
                    Text("OMNIROUTE")
                        .font(OmniTypography.appHeader)
                        .foregroundColor(OmniColors.textPrimary)
                        .tracking(0.8)
                    
                    HStack(spacing: 4) {
                        Text(viewModel.route.routeName)
                            .font(OmniTypography.metadataTag)
                            .foregroundColor(OmniColors.textSecondary)
                        
                        Image(systemName: viewModel.route.vehicleType.iconName)
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(OmniColors.textSecondary)
                    }
                }
                .padding(.top, OmniLayout.spacingXXSmall)
                
                // MARK: - Mini Map Snapshot (Watch 1)
                ZStack(alignment: .bottom) {
                    MiniMapSnapshot(
                        waypoints: viewModel.route.waypoints,
                        activeStopIndex: viewModel.currentStopIndex
                    )
                    .frame(height: 96)
                    
                    // Route progress overlay chip: "4/15 ENTREGAS ◯"
                    RouteProgressBar(
                        completedStops: viewModel.route.completedStops,
                        totalStops: viewModel.route.totalStops,
                        isCompactPill: true
                    )
                    .offset(y: 12)
                }
                
                Spacer(minLength: 12)
                
                // MARK: - Action: INICIAR RUTA
                DeliveryButton(
                    style: .primaryGreen(title: "INICIAR RUTA"),
                    action: {
                        withAnimation(.easeInOut) {
                            viewModel.startRoute()
                        }
                    }
                )
            }
            .padding(.horizontal, OmniLayout.spacingSmall)
            .padding(.bottom, OmniLayout.spacingXSmall)
        }
    }
}

// MARK: - RouteOverviewStartView Previews
#Preview("RouteOverviewStartView - Watch 1 Match") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return RouteOverviewStartView(viewModel: vm)
}
