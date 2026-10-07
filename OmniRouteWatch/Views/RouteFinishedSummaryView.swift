import SwiftUI

// MARK: - RouteFinishedSummaryView
/// Celebratory completion screen shown when all stops on the active route
/// have been fulfilled. Displays summary metrics and sync confirmation.
public struct RouteFinishedSummaryView: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    private var deliveredCount: Int {
        viewModel.route.stops.filter { $0.status == .delivered }.count
    }
    
    private var absentCount: Int {
        viewModel.route.stops.filter { $0.status == .absent }.count
    }
    
    public var body: some View {
        ZStack {
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            VStack(spacing: OmniLayout.spacingSmall) {
                // Celebration Badge
                ZStack {
                    Circle()
                        .fill(OmniColors.deliveryGreen.opacity(0.15))
                        .frame(width: 48, height: 48)
                    
                    Image(systemName: "flag.checkered")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(OmniColors.deliveryGreen)
                }
                .padding(.top, OmniLayout.spacingXXSmall)
                
                Text("¡RUTA COMPLETADA!")
                    .font(OmniTypography.actionButton)
                    .foregroundColor(OmniColors.textPrimary)
                    .textCase(.uppercase)
                
                // Metrics Card
                GlanceableCard {
                    HStack(spacing: OmniLayout.spacingLarge) {
                        VStack(spacing: 2) {
                            Text("\(deliveredCount)")
                                .font(OmniTypography.addressTitle)
                                .foregroundColor(OmniColors.deliveryGreen)
                            Text("Entregados")
                                .font(OmniTypography.metadataTag)
                                .foregroundColor(OmniColors.textSecondary)
                        }
                        
                        Divider()
                            .frame(height: 28)
                        
                        VStack(spacing: 2) {
                            Text("\(absentCount)")
                                .font(OmniTypography.addressTitle)
                                .foregroundColor(OmniColors.alertRed)
                            Text("Ausentes")
                                .font(OmniTypography.metadataTag)
                                .foregroundColor(OmniColors.textSecondary)
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
                
                Spacer(minLength: 4)
                
                // Reset / Return action
                DeliveryButton(
                    style: .custom(
                        title: "REINICIAR RUTA",
                        icon: "arrow.clockwise",
                        bg: OmniColors.cardBackground,
                        fg: OmniColors.textPrimary,
                        border: OmniColors.borderSubtle
                    ),
                    action: {
                        viewModel.resetRouteForDemo()
                    }
                )
            }
            .padding(.horizontal, OmniLayout.spacingSmall)
            .padding(.bottom, OmniLayout.spacingXSmall)
        }
    }
}

// MARK: - RouteFinishedSummaryView Previews
#Preview("RouteFinishedSummaryView Previews") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return RouteFinishedSummaryView(viewModel: vm)
}
