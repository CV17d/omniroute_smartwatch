import SwiftUI

// MARK: - IncidentReportSheet
/// Quick-report action sheet allowing couriers to report road exceptions
/// within 3 seconds using pre-classified incident chips.
public struct IncidentReportSheet: View {
    @ObservedObject public var viewModel: DeliveryRouteViewModel
    @Environment(\.dismiss) private var dismiss
    
    private let incidentTags: [String] = [
        "Dirección Errónea",
        "Zona Inaccesible",
        "Paquete Averiado",
        "Rechazado por Cliente"
    ]
    
    public init(viewModel: DeliveryRouteViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        ZStack {
            OmniColors.backgroundPrimary
                .ignoresSafeArea()
            
            VStack(spacing: OmniLayout.spacingSmall) {
                // Sheet title
                HStack(spacing: 4) {
                    Image(systemName: "exclamationmark.triangle.fill")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(OmniColors.warningAmber)
                    
                    Text("REPORTAR INCIDENCIA")
                        .font(OmniTypography.secondaryButton)
                        .foregroundColor(OmniColors.textPrimary)
                }
                .padding(.top, OmniLayout.spacingXXSmall)
                
                // Incident quick-tags
                ScrollView {
                    VStack(spacing: 6) {
                        ForEach(incidentTags, id: \.self) { tag in
                            Button(action: {
                                viewModel.reportIncident(tag: tag)
                                dismiss()
                            }) {
                                HStack {
                                    Text(tag)
                                        .font(OmniTypography.recipientDetail)
                                        .foregroundColor(OmniColors.textPrimary)
                                    Spacer()
                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(OmniColors.textTertiary)
                                }
                                .padding(.horizontal, OmniLayout.spacingMedium)
                                .padding(.vertical, 8)
                                .background(
                                    RoundedRectangle(cornerRadius: OmniLayout.radiusChip)
                                        .fill(OmniColors.cardBackground)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: OmniLayout.radiusChip)
                                        .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
                                )
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, OmniLayout.spacingSmall)
                }
                
                // Cancel button
                Button(action: { dismiss() }) {
                    Text("CANCELAR")
                        .font(OmniTypography.metadataTag)
                        .foregroundColor(OmniColors.textSecondary)
                }
                .padding(.bottom, OmniLayout.spacingXXSmall)
            }
        }
    }
}

// MARK: - IncidentReportSheet Previews
#Preview("IncidentReportSheet Previews") {
    let vm = DeliveryRouteViewModel(
        connectivityService: MockBluetoothPayloadProvider(),
        initialRoute: RouteSummary.sampleMotoRoute
    )
    return IncidentReportSheet(viewModel: vm)
}
