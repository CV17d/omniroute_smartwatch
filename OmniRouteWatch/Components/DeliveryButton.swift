import SwiftUI

// MARK: - DeliveryButton Style Enum
public enum DeliveryButtonStyle {
    case primaryGreen(title: String, icon: String? = nil)
    case deliveredLight(title: String = "ENTREGADO")
    case custom(title: String, icon: String?, bg: Color, fg: Color, border: Color?)
}

// MARK: - DeliveryButton Component
/// Massive glanceable primary action button designed for single-tap confirmation
/// during active delivery (e.g. "INICIAR RUTA", "¡LLEGUE!", "✓ ENTREGADO").
public struct DeliveryButton: View {
    public let style: DeliveryButtonStyle
    public let isEnabled: Bool
    public let isLoading: Bool
    public let action: () -> Void
    
    public init(
        style: DeliveryButtonStyle,
        isEnabled: Bool = true,
        isLoading: Bool = false,
        action: @escaping () -> Void
    ) {
        self.style = style
        self.isEnabled = isEnabled
        self.isLoading = isLoading
        self.action = action
    }
    
    public var body: some View {
        Button(action: {
            guard isEnabled && !isLoading else { return }
            action()
        }) {
            ZStack {
                switch style {
                case .primaryGreen(let title, let icon):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(OmniColors.deliveryGreen)
                    
                    HStack(spacing: OmniLayout.spacingSmall) {
                        if let icon = icon {
                            Image(systemName: icon)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                        }
                        Text(title)
                            .actionButtonTextStyle(color: .white)
                    }
                    
                case .deliveredLight(let title):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(OmniColors.cardBackground)
                        .overlay(
                            RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                                .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
                        )
                    
                    HStack(spacing: OmniLayout.spacingSmall) {
                        Image(systemName: "checkmark")
                            .font(.system(size: 16, weight: .black))
                            .foregroundColor(OmniColors.deliveryGreen)
                        
                        Text(title)
                            .font(OmniTypography.actionButton)
                            .foregroundColor(OmniColors.textPrimary)
                            .textCase(.uppercase)
                    }
                    
                case .custom(let title, let icon, let bg, let fg, let border):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(bg)
                        .overlay(
                            Group {
                                if let border = border {
                                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                                        .stroke(border, lineWidth: OmniLayout.borderHairline)
                                }
                            }
                        )
                    
                    HStack(spacing: OmniLayout.spacingSmall) {
                        if let icon = icon {
                            Image(systemName: icon)
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(fg)
                        }
                        Text(title)
                            .font(OmniTypography.actionButton)
                            .foregroundColor(fg)
                            .textCase(.uppercase)
                    }
                }
                
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                }
            }
            .frame(height: OmniLayout.primaryButtonHeight)
            .contentShape(Capsule())
        }
        .buttonStyle(PlainButtonStyle())
        .opacity(isEnabled ? 1.0 : 0.5)
        .disabled(!isEnabled || isLoading)
    }
}

// MARK: - DeliveryButton Previews
#Preview("DeliveryButton Variants") {
    VStack(spacing: 12) {
        DeliveryButton(style: .primaryGreen(title: "INICIAR RUTA")) {}
        DeliveryButton(style: .primaryGreen(title: "¡LLEGUE!")) {}
        DeliveryButton(style: .deliveredLight(title: "ENTREGADO")) {}
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
