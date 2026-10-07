import SwiftUI

// MARK: - RouteProgressBar Header Component
/// Displays route progress in two complementary glanceable formats:
/// 1. A compact chip with completion count and mini ring ("4/15 ENTREGAS", Watch 1).
/// 2. A segmented linear bar indicating completed vs remaining stops along the route.
public struct RouteProgressBar: View {
    public let completedStops: Int
    public let totalStops: Int
    public let isCompactPill: Bool
    
    public init(
        completedStops: Int,
        totalStops: Int,
        isCompactPill: Bool = true
    ) {
        self.completedStops = completedStops
        self.totalStops = max(totalStops, 1)
        self.isCompactPill = isCompactPill
    }
    
    private var ratio: Double {
        Double(completedStops) / Double(totalStops)
    }
    
    public var body: some View {
        if isCompactPill {
            // Pill chip matching Watch 1: "4/15 ENTREGAS ◯"
            HStack(spacing: OmniLayout.spacingSmall) {
                Text("\(completedStops)/\(totalStops) ENTREGAS")
                    .font(OmniTypography.badge)
                    .foregroundColor(OmniColors.textPrimary)
                    .textCase(.uppercase)
                
                CircularProgressRing(
                    progress: ratio,
                    ringDiameter: 14,
                    lineWidth: 2.2,
                    progressColor: OmniColors.deliveryGreen,
                    trackColor: OmniColors.borderSubtle
                )
            }
            .padding(.horizontal, OmniLayout.spacingMedium)
            .padding(.vertical, OmniLayout.spacingXSmall)
            .background(
                Capsule()
                    .fill(OmniColors.cardBackground)
            )
            .overlay(
                Capsule()
                    .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
            )
        } else {
            // Segmented linear bar for top navigation status
            VStack(spacing: OmniLayout.spacingXXSmall) {
                HStack(spacing: OmniLayout.spacingXXSmall) {
                    ForEach(0..<totalStops, id: \.self) { index in
                        RoundedRectangle(cornerRadius: 1.5)
                            .fill(index < completedStops ? OmniColors.deliveryGreen : OmniColors.borderSubtle)
                            .frame(height: 3)
                    }
                }
            }
            .padding(.horizontal, OmniLayout.spacingXSmall)
        }
    }
}

// MARK: - RouteProgressBar Previews
#Preview("RouteProgressBar Previews") {
    VStack(spacing: 16) {
        RouteProgressBar(completedStops: 4, totalStops: 15, isCompactPill: true)
        RouteProgressBar(completedStops: 4, totalStops: 15, isCompactPill: false)
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
