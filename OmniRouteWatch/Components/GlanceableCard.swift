import SwiftUI

// MARK: - GlanceableCard Container
/// A card container with subtle borders and light-mode background styling,
/// providing high visual separation and glanceability on Apple Watch.
public struct GlanceableCard<Content: View>: View {
    private let content: Content
    private let backgroundColor: Color
    private let borderColor: Color
    private let cornerRadius: CGFloat
    private let contentPadding: EdgeInsets
    
    public init(
        backgroundColor: Color = OmniColors.cardBackground,
        borderColor: Color = OmniColors.borderSubtle,
        cornerRadius: CGFloat = OmniLayout.radiusCard,
        contentPadding: EdgeInsets = EdgeInsets(
            top: OmniLayout.spacingSmall,
            leading: OmniLayout.spacingMedium,
            bottom: OmniLayout.spacingSmall,
            trailing: OmniLayout.spacingMedium
        ),
        @ViewBuilder content: () -> Content
    ) {
        self.backgroundColor = backgroundColor
        self.borderColor = borderColor
        self.cornerRadius = cornerRadius
        self.contentPadding = contentPadding
        self.content = content()
    }
    
    public var body: some View {
        content
            .padding(contentPadding)
            .background(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .fill(backgroundColor)
            )
            .overlay(
                RoundedRectangle(cornerRadius: cornerRadius)
                    .stroke(borderColor, lineWidth: OmniLayout.borderHairline)
            )
    }
}

// MARK: - GlanceableCard Previews
#Preview("GlanceableCard Styles") {
    VStack(spacing: 12) {
        GlanceableCard {
            VStack(alignment: .leading, spacing: 4) {
                Text("CALLE 22 # 5-43")
                    .font(OmniTypography.addressTitle)
                    .foregroundColor(OmniColors.textPrimary)
                Text("Envia • Apt 302")
                    .font(OmniTypography.recipientDetail)
                    .foregroundColor(OmniColors.textSecondary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
