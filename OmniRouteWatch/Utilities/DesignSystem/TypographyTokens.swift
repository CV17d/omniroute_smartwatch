import SwiftUI

// MARK: - OmniRoute Typography Tokens (Glanceable Watch Display)
/// Typography extensions and styles optimized for quick 3-5 second glances
/// during motorcycle delivery navigation on Apple Watch screen sizes (40mm to 49mm Ultra).
public enum OmniTypography {
    /// App title and prominent headers (e.g. "OMNIROUTE")
    public static let appHeader = Font.system(size: 15, weight: .black, design: .rounded)
    
    /// Large high-contrast street address (e.g. "CALLE 22 # 5-43")
    public static let addressTitle = Font.system(size: 17, weight: .heavy, design: .default)
    
    /// Medium address or stop detail
    public static let addressSubtitle = Font.system(size: 13, weight: .semibold, design: .default)
    
    /// Distance and navigation instruction (e.g. "150m - Continuar abajo")
    public static let navigationInstruction = Font.system(size: 12, weight: .medium, design: .default)
    
    /// Massive button label text (e.g. "¡LLEGUE!", "INICIAR RUTA")
    public static let actionButton = Font.system(size: 16, weight: .bold, design: .rounded)
    
    /// Secondary action button text (e.g. "INCIDENCIA", "AUSENTE")
    public static let secondaryButton = Font.system(size: 13, weight: .bold, design: .rounded)
    
    /// Metrics and progress badge text (e.g. "4/15 ENTREGAS")
    public static let badge = Font.system(size: 11, weight: .bold, design: .rounded)
    
    /// Route transport type and tags (e.g. "RUTA: MOTO", "MOTO")
    public static let metadataTag = Font.system(size: 10, weight: .bold, design: .rounded)
    
    /// Recipient, apartment and package notes (e.g. "Envia • Apt 302")
    public static let recipientDetail = Font.system(size: 12, weight: .regular, design: .default)
}

// MARK: - View Modifiers for Glanceable Text
public struct GlanceableAddressModifier: ViewModifier {
    public func body(content: Content) -> some View {
        content
            .font(OmniTypography.addressTitle)
            .foregroundColor(OmniColors.textPrimary)
            .lineLimit(2)
            .minimumScaleFactor(0.85)
            .multilineTextAlignment(.center)
    }
}

public struct ActionButtonTextModifier: ViewModifier {
    let color: Color
    
    public init(color: Color = .white) {
        self.color = color
    }
    
    public func body(content: Content) -> some View {
        content
            .font(OmniTypography.actionButton)
            .foregroundColor(color)
            .textCase(.uppercase)
            .tracking(0.5)
    }
}

extension View {
    public func glanceableAddressStyle() -> some View {
        self.modifier(GlanceableAddressModifier())
    }
    
    public func actionButtonTextStyle(color: Color = .white) -> some View {
        self.modifier(ActionButtonTextModifier(color: color))
    }
}
