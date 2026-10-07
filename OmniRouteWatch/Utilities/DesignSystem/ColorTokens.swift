import SwiftUI

// MARK: - OmniRoute Color Tokens (Calibrated to Reference Image)
/// Fine-tuned Light Mode color tokens matching the exact contrast,
/// button pills, and map tones shown in the OmniRoute watchOS reference design.
public enum OmniColors {
    // MARK: - Delivery Actions (Watch 1 & 2 Green)
    /// High-visibility emerald green (#22B061) matching "INICIAR RUTA" and "¡LLEGUE!"
    public static let deliveryGreen = Color(red: 0.133, green: 0.690, blue: 0.380)
    /// Deep forest accent for high contrast against light surfaces
    public static let deliveryGreenDark = Color(red: 0.086, green: 0.520, blue: 0.280)
    /// Subtle tint for delivered badges and success rings
    public static let deliveryGreenSubtle = Color(red: 0.133, green: 0.690, blue: 0.380).opacity(0.14)
    
    // MARK: - Alert & Incident Actions (Watch 2 & 3)
    /// Alert red (#E03838) matching the "✗ NO ENTREGADO" glyph
    public static let alertRed = Color(red: 0.878, green: 0.220, blue: 0.220)
    /// Cautionary amber (#F29927) matching the "INCIDENCIA ⚠️" warning glyph
    public static let warningAmber = Color(red: 0.949, green: 0.600, blue: 0.153)
    
    // MARK: - Glanceable Light Mode Surfaces
    /// Crisp pure white base canvas (#FFFFFF)
    public static let backgroundPrimary = Color.white
    /// Soft grey canvas for secondary grouped cards (#F8F9FA)
    public static let backgroundSecondary = Color(red: 0.972, green: 0.976, blue: 0.980)
    /// Pill button surface (#ECEFF1 / #EEF0F4) for "✓ ENTREGADO" and "INCIDENCIA"
    public static let cardBackground = Color(red: 0.925, green: 0.937, blue: 0.949)
    /// Elevated light container
    public static let cardBackgroundElevated = Color(red: 0.890, green: 0.905, blue: 0.920)
    
    // MARK: - Typography & High-Contrast Strokes
    /// High-contrast rich charcoal black (#121417) for "CALLE 22 # 5-43"
    public static let textPrimary = Color(red: 0.070, green: 0.078, blue: 0.090)
    /// Readable slate grey (#5B6471) for subtitles and navigation instructions
    public static let textSecondary = Color(red: 0.357, green: 0.392, blue: 0.443)
    /// Tertiary slate for separators (#8A94A6)
    public static let textTertiary = Color(red: 0.541, green: 0.580, blue: 0.651)
    
    // MARK: - Precision Borders
    /// Calibrated stroke (#D5DAE1) for pill borders and cards
    public static let borderSubtle = Color(red: 0.835, green: 0.855, blue: 0.882)
    /// High-contrast outline for interactive buttons
    public static let borderStrong = Color(red: 0.720, green: 0.745, blue: 0.780)
    
    // MARK: - Vector Map Palette (Watch 1)
    /// Land surface beige/sand (#F4F1EA)
    public static let mapLand = Color(red: 0.957, green: 0.945, blue: 0.918)
    /// Coastal water blue (#B8DCED)
    public static let mapWater = Color(red: 0.722, green: 0.863, blue: 0.929)
    /// Route polyline vibrant green
    public static let routeLine = Color(red: 0.133, green: 0.690, blue: 0.380)
    /// Waypoint pin fill
    public static let waypointFill = Color(red: 0.133, green: 0.690, blue: 0.380)
    /// Waypoint pin border
    public static let waypointBorder = Color.white
}

extension Color {
    public static let omniDeliveryGreen = OmniColors.deliveryGreen
    public static let omniAlertRed = OmniColors.alertRed
    public static let omniWarningAmber = OmniColors.warningAmber
    public static let omniBackgroundPrimary = OmniColors.backgroundPrimary
    public static let omniBackgroundSecondary = OmniColors.backgroundSecondary
    public static let omniCardBackground = OmniColors.cardBackground
    public static let omniTextPrimary = OmniColors.textPrimary
    public static let omniTextSecondary = OmniColors.textSecondary
    public static let omniBorderSubtle = OmniColors.borderSubtle
}
