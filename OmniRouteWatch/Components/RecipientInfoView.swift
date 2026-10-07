import SwiftUI

// MARK: - RecipientInfoView Component
/// Renders delivery recipient details and apartment/unit info
/// (e.g. "Envia • Apt 302") underneath the prominent address.
public struct RecipientInfoView: View {
    public let recipientName: String
    public let apartmentOrSuite: String?
    public let packageId: String?
    
    public init(
        recipientName: String,
        apartmentOrSuite: String? = nil,
        packageId: String? = nil
    ) {
        self.recipientName = recipientName
        self.apartmentOrSuite = apartmentOrSuite
        self.packageId = packageId
    }
    
    public var body: some View {
        VStack(spacing: OmniLayout.spacingXXSmall) {
            HStack(spacing: OmniLayout.spacingXSmall) {
                Text(recipientName)
                    .font(OmniTypography.recipientDetail)
                    .foregroundColor(OmniColors.textSecondary)
                
                if let apt = apartmentOrSuite, !apt.isEmpty {
                    Text("•")
                        .font(OmniTypography.recipientDetail)
                        .foregroundColor(OmniColors.textTertiary)
                    
                    Text(apt)
                        .font(OmniTypography.recipientDetail)
                        .foregroundColor(OmniColors.textSecondary)
                }
            }
            .lineLimit(1)
            
            if let package = packageId, !package.isEmpty {
                Text(package)
                    .font(OmniTypography.metadataTag)
                    .foregroundColor(OmniColors.textTertiary)
                    .lineLimit(1)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

// MARK: - RecipientInfoView Previews
#Preview("RecipientInfoView Previews") {
    VStack(spacing: 12) {
        RecipientInfoView(recipientName: "Envia", apartmentOrSuite: "Apt 302")
        RecipientInfoView(recipientName: "Carlos Mendez", apartmentOrSuite: "Oficina 501", packageId: "ENV-2024-991")
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
