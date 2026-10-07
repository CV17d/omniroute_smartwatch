import SwiftUI

// MARK: - ActiveDeliveryView (Watch 2)
/// Faithfully clones the active navigation delivery screen displayed on Watch 2:
/// - Top header: direction glyph, distance, instruction, and vehicle badge ("MOTO")
/// - Hero center: prominent high-contrast address ("CALLE 22 # 5-43") and recipient ("Envia • Apt 302")
/// - Primary action: massive green pill button ("¡LLEGUE!")
/// - Secondary action: incident reporting button ("INCIDENCIA ⚠️")
public struct ActiveDeliveryView: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        ZStack {
            // High-contrast clean white background for outdoor readability
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            if let stop = viewModel.currentStop {
                VStack(spacing: OmniLayout.spacingSmall) {
                    // MARK: - Top Navigation Header
                    StopHeaderView(
                        direction: stop.turnDirection,
                        distanceText: stop.distanceText,
                        instruction: stop.navigationInstruction,
                        vehicleBadge: viewModel.route.vehicleType.rawValue
                    )
                    
                    Spacer(minLength: 2)
                    
                    // MARK: - Center Hero Address & Recipient Card
                    VStack(spacing: OmniLayout.spacingXXSmall + 1) {
                        AddressCompactView(address: stop.address, isEmphasized: true)
                        
                        RecipientInfoView(
                            recipientName: stop.recipientName,
                            apartmentOrSuite: stop.apartmentOrSuite,
                            packageId: stop.packageId
                        )
                    }
                    .padding(.vertical, OmniLayout.spacingXSmall)
                    
                    Spacer(minLength: 2)
                    
                    // MARK: - Primary Action: "¡LLEGUE!"
                    DeliveryButton(
                        style: .primaryGreen(title: "¡LLEGUE!"),
                        action: {
                            viewModel.arriveAtStop()
                        }
                    )
                    
                    // MARK: - Secondary Action: "INCIDENCIA ⚠️"
                    SecondaryActionButton(
                        style: .incident(),
                        action: {
                            viewModel.showIncidentSheet = true
                        }
                    )
                }
                .padding(.horizontal, OmniLayout.spacingSmall)
                .padding(.bottom, OmniLayout.spacingXSmall)
            } else {
                // Route Completed or No Active Stops
                RouteFinishedSummaryView(viewModel: viewModel)
            }
        }
        .sheet(isPresented: $viewModel.showConfirmationModal) {
            DeliveryConfirmationModal(viewModel: viewModel)
        }
        .sheet(isPresented: $viewModel.showIncidentSheet) {
            IncidentReportSheet(viewModel: viewModel)
        }
    }
}

// MARK: - ActiveDeliveryView Previews
#Preview("ActiveDeliveryView - Watch 2 Match") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return ActiveDeliveryView(viewModel: vm)
}
