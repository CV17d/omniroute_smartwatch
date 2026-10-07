import SwiftUI

// MARK: - StatusBadge Component
/// A compact, high-contrast status badge used to render delivery state,
/// transport mode (e.g. "MOTO"), or stop progress count.
public struct StatusBadge: View {
    public let title: String
    public let icon: String?
    public let foregroundColor: Color
    public let backgroundColor: Color
    public let borderColor: Color?
    
    public init(
        title: String,
        icon: String? = nil,
        foregroundColor: Color = OmniColors.textPrimary,
        backgroundColor: Color = OmniColors.cardBackground,
        borderColor: Color? = OmniColors.borderSubtle
    ) {
        self.title = title
        self.icon = icon
        self.foregroundColor = foregroundColor
        self.backgroundColor = backgroundColor
        self.borderColor = borderColor
    }
    
    public var body: some View {
        HStack(spacing: OmniLayout.spacingXSmall) {
            if let icon = icon {
                Image(systemName: icon)
                    .font(.system(size: 9, weight: .bold))
            }
            Text(title)
                .font(OmniTypography.metadataTag)
        }
        .foregroundColor(foregroundColor)
        .padding(.horizontal, OmniLayout.spacingSmall)
        .padding(.vertical, OmniLayout.spacingXXSmall + 1)
        .background(
            RoundedRectangle(cornerRadius: OmniLayout.radiusChip)
                .fill(backgroundColor)
        )
        .overlay(
            Group {
                if let borderColor = borderColor {
                    RoundedRectangle(cornerRadius: OmniLayout.radiusChip)
                        .stroke(borderColor, lineWidth: OmniLayout.borderHairline)
                }
            }
        )
    }
}

// MARK: - StatusBadge Previews
#Preview("StatusBadge Variants") {
    VStack(spacing: 8) {
        StatusBadge(title: "MOTO", icon: "bicycle")
        StatusBadge(title: "ENTREGADO", icon: "checkmark", foregroundColor: OmniColors.deliveryGreen, backgroundColor: OmniColors.deliveryGreen.opacity(0.15))
        StatusBadge(title: "AUSENTE", icon: "xmark", foregroundColor: OmniColors.alertRed, backgroundColor: OmniColors.alertRed.opacity(0.15))
        StatusBadge(title: "4/15 ENTREGAS", icon: "cube.box")
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
