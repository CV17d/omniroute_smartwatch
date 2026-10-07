import SwiftUI

// MARK: - AddressCompactView Component
/// High-contrast address display engineered for instant 1-second legibility
/// under bright sunlight conditions on Apple Watch.
public struct AddressCompactView: View {
    public let address: String
    public let isEmphasized: Bool
    
    public init(address: String, isEmphasized: Bool = true) {
        self.address = address
        self.isEmphasized = isEmphasized
    }
    
    public var body: some View {
        Text(address)
            .font(isEmphasized ? OmniTypography.addressTitle : OmniTypography.addressSubtitle)
            .foregroundColor(OmniColors.textPrimary)
            .multilineTextAlignment(.center)
            .lineLimit(2)
            .minimumScaleFactor(0.8)
            .padding(.horizontal, OmniLayout.spacingSmall)
            .frame(maxWidth: .infinity)
    }
}

// MARK: - AddressCompactView Previews
#Preview("AddressCompactView Previews") {
    VStack(spacing: 16) {
        AddressCompactView(address: "CALLE 22 # 5-43")
        AddressCompactView(address: "CARRERA 7 # 32-16 SUR", isEmphasized: false)
        AddressCompactView(address: "AVENIDA EL DORADO # 68C-61 EDIFICIO CENTRO")
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
