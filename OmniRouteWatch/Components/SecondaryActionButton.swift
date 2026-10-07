import SwiftUI

// MARK: - SecondaryActionStyle
public enum SecondaryActionStyle {
    case incident(title: String = "INCIDENCIA")
    case absent(title: String = "NO ENTREGADO", subtitle: String = "AUSENTE")
    case outline(title: String, icon: String?, tint: Color)
}

// MARK: - SecondaryActionButton Component
/// Secondary action button for negative states or alerts,
/// such as "INCIDENCIA ⚠️" (Watch 2) and "✗ NO ENTREGADO / AUSENTE" (Watch 3).
public struct SecondaryActionButton: View {
    public let style: SecondaryActionStyle
    public let isEnabled: Bool
    public let action: () -> Void
    
    public init(
        style: SecondaryActionStyle,
        isEnabled: Bool = true,
        action: @escaping () -> Void
    ) {
        self.style = style
        self.isEnabled = isEnabled
        self.action = action
    }
    
    public var body: some View {
        Button(action: {
            guard isEnabled else { return }
            action()
        }) {
            ZStack {
                switch style {
                case .incident(let title):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(OmniColors.cardBackground)
                        .overlay(
                            RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                                .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
                        )
                    
                    HStack(spacing: OmniLayout.spacingSmall) {
                        Text(title)
                            .font(OmniTypography.secondaryButton)
                            .foregroundColor(OmniColors.textPrimary)
                            .textCase(.uppercase)
                        
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(OmniColors.warningAmber)
                    }
                    
                case .absent(let title, let subtitle):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(OmniColors.cardBackground)
                        .overlay(
                            RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                                .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
                        )
                    
                    VStack(spacing: 1) {
                        HStack(spacing: OmniLayout.spacingSmall) {
                            Image(systemName: "xmark")
                                .font(.system(size: 15, weight: .black))
                                .foregroundColor(OmniColors.alertRed)
                            
                            Text(title)
                                .font(OmniTypography.actionButton)
                                .foregroundColor(OmniColors.textPrimary)
                                .textCase(.uppercase)
                        }
                        
                        Text(subtitle)
                            .font(.system(size: 10, weight: .bold, design: .rounded))
                            .foregroundColor(OmniColors.textSecondary)
                            .textCase(.uppercase)
                    }
                    
                case .outline(let title, let icon, let tint):
                    RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                        .fill(Color.clear)
                        .overlay(
                            RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                                .stroke(tint, lineWidth: OmniLayout.borderSubtle)
                        )
                    
                    HStack(spacing: OmniLayout.spacingSmall) {
                        if let icon = icon {
                            Image(systemName: icon)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(tint)
                        }
                        Text(title)
                            .font(OmniTypography.secondaryButton)
                            .foregroundColor(tint)
                    }
                }
            }
            .frame(height: OmniLayout.primaryButtonHeight)
            .contentShape(Capsule())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!isEnabled)
    }
}

// MARK: - SecondaryActionButton Previews
#Preview("SecondaryActionButton Previews") {
    VStack(spacing: 12) {
        SecondaryActionButton(style: .incident()) {}
        SecondaryActionButton(style: .absent()) {}
        SecondaryActionButton(style: .outline(title: "SALTAR PARADA", icon: "arrow.right", tint: OmniColors.textSecondary)) {}
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
