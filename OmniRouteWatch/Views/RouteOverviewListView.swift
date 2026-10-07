import SwiftUI

// MARK: - RouteOverviewListView
/// Scrollable glanceable overview showing all stops in the route,
/// their current completion status, and permitting stop re-selection.
public struct RouteOverviewListView: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    @Environment(\.dismiss) private var dismiss
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        ZStack {
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            VStack(spacing: OmniLayout.spacingSmall) {
                // Header with Route Name & Progress
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(viewModel.route.routeName)
                            .font(OmniTypography.metadataTag)
                            .foregroundColor(OmniColors.textSecondary)
                        
                        Text(viewModel.route.glanceableProgressLabel)
                            .font(OmniTypography.actionButton)
                            .foregroundColor(OmniColors.textPrimary)
                    }
                    
                    Spacer()
                    
                    CircularProgressRing(
                        progress: viewModel.route.progressRatio,
                        ringDiameter: 22,
                        lineWidth: 3.0
                    )
                }
                .padding(.horizontal, OmniLayout.spacingMedium)
                .padding(.top, OmniLayout.spacingXSmall)
                
                // Stops List
                ScrollView {
                    LazyVStack(spacing: OmniLayout.spacingSmall) {
                        ForEach(Array(viewModel.route.stops.enumerated()), id: \.element.id) { index, stop in
                            Button(action: {
                                viewModel.selectStop(id: stop.id)
                                dismiss()
                            }) {
                                GlanceableCard(
                                    backgroundColor: index == viewModel.currentStopIndex
                                        ? OmniColors.deliveryGreen.opacity(0.12)
                                        : OmniColors.cardBackground,
                                    borderColor: index == viewModel.currentStopIndex
                                        ? OmniColors.deliveryGreen
                                        : OmniColors.borderSubtle
                                ) {
                                    HStack(spacing: OmniLayout.spacingSmall) {
                                        // Stop Number
                                        Text("#\(stop.stopNumber)")
                                            .font(OmniTypography.badge)
                                            .foregroundColor(OmniColors.textSecondary)
                                            .frame(width: 24, alignment: .leading)
                                        
                                        // Address & Recipient
                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(stop.address)
                                                .font(OmniTypography.addressSubtitle)
                                                .foregroundColor(OmniColors.textPrimary)
                                                .lineLimit(1)
                                            
                                            Text(stop.glanceableSubtitle)
                                                .font(OmniTypography.metadataTag)
                                                .foregroundColor(OmniColors.textSecondary)
                                                .lineLimit(1)
                                        }
                                        
                                        Spacer()
                                        
                                        // Status Icon
                                        Image(systemName: stop.status.systemIcon)
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundColor(stop.status.statusColor)
                                    }
                                }
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, OmniLayout.spacingSmall)
                    .padding(.bottom, OmniLayout.spacingMedium)
                }
            }
        }
    }
}

// MARK: - RouteOverviewListView Previews
#Preview("RouteOverviewListView Previews") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return RouteOverviewListView(viewModel: vm)
}
