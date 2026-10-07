import SwiftUI

// MARK: - EstimatedArrivalBadge Component
/// Glanceable ETA badge showing estimated arrival minutes and distance
/// formatted compactly for watch navigation.
public struct EstimatedArrivalBadge: View {
    public let minutes: Int
    public let distanceText: String
    
    public init(minutes: Int, distanceText: String = "150m") {
        self.minutes = minutes
        self.distanceText = distanceText
    }
    
    public var body: some View {
        HStack(spacing: OmniLayout.spacingXSmall) {
            Image(systemName: "timer")
                .font(.system(size: 10, weight: .semibold))
                .foregroundColor(OmniColors.deliveryGreen)
            
            Text("\(minutes) min")
                .font(OmniTypography.metadataTag)
                .foregroundColor(OmniColors.textPrimary)
            
            Text("•")
                .font(OmniTypography.metadataTag)
                .foregroundColor(OmniColors.textTertiary)
            
            Text(distanceText)
                .font(OmniTypography.metadataTag)
                .foregroundColor(OmniColors.textSecondary)
        }
        .padding(.horizontal, OmniLayout.spacingSmall)
        .padding(.vertical, OmniLayout.spacingXXSmall + 2)
        .background(
            Capsule()
                .fill(OmniColors.cardBackground)
        )
        .overlay(
            Capsule()
                .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
        )
    }
}

// MARK: - EstimatedArrivalBadge Previews
#Preview("EstimatedArrivalBadge Previews") {
    VStack(spacing: 8) {
        EstimatedArrivalBadge(minutes: 2, distanceText: "150m")
        EstimatedArrivalBadge(minutes: 5, distanceText: "600m")
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
