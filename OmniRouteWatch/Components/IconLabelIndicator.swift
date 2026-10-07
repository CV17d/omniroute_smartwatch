import SwiftUI

// MARK: - IconLabelIndicator Atom
/// An atomic indicator pairing an SF Symbol or custom icon with high-contrast text,
/// such as the navigation turn arrow (`↓ 150m - Continuar abajo`) seen on Watch 2.
public struct IconLabelIndicator: View {
    public let systemImage: String
    public let title: String
    public let iconColor: Color
    public let textColor: Color
    public let iconSize: CGFloat
    public let font: Font
    public let spacing: CGFloat
    
    public init(
        systemImage: String,
        title: String,
        iconColor: Color = OmniColors.textPrimary,
        textColor: Color = OmniColors.textPrimary,
        iconSize: CGFloat = 16,
        font: Font = OmniTypography.navigationInstruction,
        spacing: CGFloat = OmniLayout.spacingSmall
    ) {
        self.systemImage = systemImage
        self.title = title
        self.iconColor = iconColor
        self.textColor = textColor
        self.iconSize = iconSize
        self.font = font
        self.spacing = spacing
    }
    
    public var body: some View {
        HStack(spacing: spacing) {
            Image(systemName: systemImage)
                .font(.system(size: iconSize, weight: .bold))
                .foregroundColor(iconColor)
            
            Text(title)
                .font(font)
                .foregroundColor(textColor)
                .lineLimit(1)
        }
    }
}

// MARK: - IconLabelIndicator Previews
#Preview("IconLabelIndicator Variations") {
    VStack(alignment: .leading, spacing: 12) {
        IconLabelIndicator(
            systemImage: "arrow.down",
            title: "150m - Continuar abajo",
            iconSize: 20,
            font: OmniTypography.navigationInstruction
        )
        
        IconLabelIndicator(
            systemImage: "exclamationmark.triangle.fill",
            title: "INCIDENCIA",
            iconColor: OmniColors.warningAmber,
            textColor: OmniColors.textPrimary,
            font: OmniTypography.secondaryButton
        )
        
        IconLabelIndicator(
            systemImage: "checkmark.circle.fill",
            title: "ENTREGADO",
            iconColor: OmniColors.deliveryGreen,
            textColor: OmniColors.deliveryGreenDark,
            font: OmniTypography.actionButton
        )
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
