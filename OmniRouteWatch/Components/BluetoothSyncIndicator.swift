import SwiftUI

// MARK: - BluetoothSyncIndicator Component
/// Compact visual indicator of Bluetooth link status with the companion phone.
/// Can be positioned in nav bars or headers to reassure riders of sync health.
public struct BluetoothSyncIndicator: View {
    public let state: ConnectivityState
    public let showLabel: Bool
    
    public init(state: ConnectivityState, showLabel: Bool = false) {
        self.state = state
        self.showLabel = showLabel
    }
    
    private var glyphName: String {
        switch state {
        case .connected: return "bolt.horizontal.fill"
        case .connecting: return "arrow.triangle.2.circlepath"
        case .disconnected: return "bolt.horizontal.circle"
        case .error: return "exclamationmark.circle.fill"
        }
    }
    
    private var glyphColor: Color {
        switch state {
        case .connected: return OmniColors.deliveryGreen
        case .connecting: return OmniColors.warningAmber
        case .disconnected: return OmniColors.textTertiary
        case .error: return OmniColors.alertRed
        }
    }
    
    public var body: some View {
        HStack(spacing: OmniLayout.spacingXSmall) {
            Image(systemName: glyphName)
                .font(.system(size: 11, weight: .semibold))
                .foregroundColor(glyphColor)
            
            if showLabel {
                Text(state.glanceableLabel)
                    .font(OmniTypography.metadataTag)
                    .foregroundColor(glyphColor)
            }
        }
        .padding(.horizontal, 6)
        .padding(.vertical, 3)
        .background(
            Capsule()
                .fill(OmniColors.cardBackground)
        )
    }
}

// MARK: - BluetoothSyncIndicator Previews
#Preview("BluetoothSyncIndicator States") {
    HStack(spacing: 8) {
        BluetoothSyncIndicator(state: .connected, showLabel: true)
        BluetoothSyncIndicator(state: .connecting, showLabel: true)
        BluetoothSyncIndicator(state: .disconnected, showLabel: true)
        BluetoothSyncIndicator(state: .error, showLabel: true)
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
