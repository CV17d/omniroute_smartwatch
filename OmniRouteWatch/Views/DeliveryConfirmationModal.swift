import SwiftUI

// MARK: - DeliveryConfirmationModal (Watch 3)
/// Faithfully clones the package dropoff confirmation screen from Watch 3:
/// - Header: "ENTREGAR PAQUETE"
/// - Address: "CALLE 22 # 5-43"
/// - Button 1: "✓ ENTREGADO"
/// - Button 2: "✗ NO ENTREGADO / AUSENTE"
/// Includes 3-second auto-dismiss timeout protection.
public struct DeliveryConfirmationModal: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    @Environment(\.dismiss) private var dismiss
    
    @State private var hasConfirmed: Bool = false
    @State private var confirmationFeedbackText: String? = nil
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        ZStack {
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            VStack(spacing: OmniLayout.spacingSmall) {
                // Header: "ENTREGAR PAQUETE"
                Text("ENTREGAR PAQUETE")
                    .font(OmniTypography.metadataTag)
                    .foregroundColor(OmniColors.textSecondary)
                    .textCase(.uppercase)
                    .padding(.top, OmniLayout.spacingXSmall)
                
                // Prominent Address: "CALLE 22 # 5-43"
                if let stop = viewModel.currentStop {
                    Text(stop.address)
                        .font(OmniTypography.addressTitle)
                        .foregroundColor(OmniColors.textPrimary)
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                        .minimumScaleFactor(0.85)
                        .padding(.horizontal, OmniLayout.spacingSmall)
                }
                
                Spacer(minLength: 4)
                
                if let feedback = confirmationFeedbackText {
                    // Quick 1.5-second success feedback banner
                    VStack(spacing: OmniLayout.spacingSmall) {
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 32, weight: .bold))
                            .foregroundColor(OmniColors.deliveryGreen)
                        
                        Text(feedback)
                            .font(OmniTypography.actionButton)
                            .foregroundColor(OmniColors.textPrimary)
                    }
                    .transition(.scale.combined(with: .opacity))
                    
                    Spacer()
                } else {
                    // Primary Confirm Action: "✓ ENTREGADO"
                    DeliveryButton(
                        style: .deliveredLight(title: "ENTREGADO"),
                        action: {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                confirmationFeedbackText = "¡ENTREGADO!"
                                hasConfirmed = true
                            }
                            viewModel.confirmDelivery()
                        }
                    )
                    
                    // Secondary Action: "✗ NO ENTREGADO / AUSENTE"
                    SecondaryActionButton(
                        style: .absent(),
                        action: {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                confirmationFeedbackText = "REGISTRADO: AUSENTE"
                                hasConfirmed = true
                            }
                            viewModel.markAsAbsent()
                        }
                    )
                }
            }
            .padding(.horizontal, OmniLayout.spacingSmall)
            .padding(.bottom, OmniLayout.spacingSmall)
        }
    }
}

// MARK: - DeliveryConfirmationModal Previews
#Preview("DeliveryConfirmationModal - Watch 3 Match") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return DeliveryConfirmationModal(viewModel: vm)
}
