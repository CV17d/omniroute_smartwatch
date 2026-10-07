import SwiftUI

// MARK: - StopHeaderView Component
/// Top glanceable header displayed during navigation (seen in Watch 2),
/// rendering direction icon, distance, turn instruction, and vehicle badge ("MOTO").
public struct StopHeaderView: View {
    public let direction: NavigationDirection
    public let distanceText: String
    public let instruction: String
    public let vehicleBadge: String
    
    public init(
        direction: NavigationDirection = .down,
        distanceText: String = "150m",
        instruction: String = "Continuar abajo",
        vehicleBadge: String = "MOTO"
    ) {
        self.direction = direction
        self.distanceText = distanceText
        self.instruction = instruction
        self.vehicleBadge = vehicleBadge
    }
    
    public var body: some View {
        HStack(alignment: .center, spacing: OmniLayout.spacingSmall) {
            // Direction arrow glyph
            Image(systemName: direction.systemIcon)
                .font(.system(size: 22, weight: .heavy))
                .foregroundColor(OmniColors.textPrimary)
                .frame(width: 24, height: 24)
            
            // Turn instruction & distance
            VStack(alignment: .leading, spacing: 1) {
                Text("\(distanceText) - \(instruction)")
                    .font(OmniTypography.navigationInstruction)
                    .foregroundColor(OmniColors.textPrimary)
                    .lineLimit(1)
            }
            
            Spacer(minLength: 4)
            
            // Vehicle badge (e.g. MOTO)
            Text(vehicleBadge)
                .font(OmniTypography.metadataTag)
                .foregroundColor(OmniColors.textSecondary)
                .textCase(.uppercase)
                .padding(.horizontal, 6)
                .padding(.vertical, 2)
                .background(
                    RoundedRectangle(cornerRadius: 4)
                        .fill(OmniColors.cardBackground)
                )
        }
        .padding(.horizontal, OmniLayout.spacingSmall)
        .padding(.vertical, OmniLayout.spacingXSmall)
    }
}

// MARK: - StopHeaderView Previews
#Preview("StopHeaderView Previews") {
    VStack(spacing: 12) {
        StopHeaderView(
            direction: .down,
            distanceText: "150m",
            instruction: "Continuar abajo",
            vehicleBadge: "MOTO"
        )
        
        StopHeaderView(
            direction: .turnRight,
            distanceText: "600m",
            instruction: "Girar a la derecha",
            vehicleBadge: "MOTO"
        )
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
