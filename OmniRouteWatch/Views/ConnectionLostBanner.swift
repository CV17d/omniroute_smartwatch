import SwiftUI

// MARK: - ConnectionLostBanner
/// A non-intrusive glanceable banner notifying the courier that the Bluetooth link
/// to the companion iPhone is temporarily unavailable, reassuring that actions are safely queued.
public struct ConnectionLostBanner: View {
    public let onRetry: () -> Void
    
    public init(onRetry: @escaping () -> Void) {
        self.onRetry = onRetry
    }
    
    public var body: some View {
        HStack(spacing: OmniLayout.spacingSmall) {
            Image(systemName: "bolt.slash.fill")
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(OmniColors.alertRed)
            
            VStack(alignment: .leading, spacing: 1) {
                Text("MODO OFFLINE")
                    .font(OmniTypography.metadataTag)
                    .foregroundColor(OmniColors.alertRed)
                
                Text("Guardando entregas en reloj")
                    .font(.system(size: 10, weight: .regular))
                    .foregroundColor(OmniColors.textSecondary)
            }
            
            Spacer()
            
            Button(action: onRetry) {
                Image(systemName: "arrow.clockwise")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundColor(OmniColors.textPrimary)
                    .padding(6)
                    .background(
                        Circle()
                            .fill(OmniColors.cardBackground)
                    )
            }
            .buttonStyle(PlainButtonStyle())
        }
        .padding(.horizontal, OmniLayout.spacingMedium)
        .padding(.vertical, OmniLayout.spacingSmall)
        .background(
            RoundedRectangle(cornerRadius: OmniLayout.radiusCard)
                .fill(OmniColors.alertRed.opacity(0.12))
        )
        .overlay(
            RoundedRectangle(cornerRadius: OmniLayout.radiusCard)
                .stroke(OmniColors.alertRed.opacity(0.3), lineWidth: OmniLayout.borderSubtle)
        )
    }
}

// MARK: - ConnectionLostBanner Previews
#Preview("ConnectionLostBanner Previews") {
    VStack {
        ConnectionLostBanner(onRetry: {})
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
