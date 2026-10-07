import SwiftUI

// MARK: - OmniRoute Color Tokens (Light Mode Glanceable Palette)
/// Color palette tokens tailored for high-contrast visibility on Apple Watch
/// under outdoor daylight conditions according to OmniRoute's design specifications.
public enum OmniColors {
    // MARK: - Brand & Delivery Actions
    /// Vibrant primary green used for "INICIAR RUTA", "¡LLEGUE!" and successful deliveries
    public static let deliveryGreen = Color(red: 0.13, green: 0.69, blue: 0.38) // #22B061
    /// Darker shade for pressed states and contrast accents
    public static let deliveryGreenDark = Color(red: 0.09, green: 0.55, blue: 0.30)
    
    // MARK: - Alerts & Negative Actions
    /// Alert red used for "NO ENTREGADO", "AUSENTE" and failure states
    public static let alertRed = Color(red: 0.88, green: 0.22, blue: 0.22) // #E03838
    /// Warning amber used for incident buttons and cautionary states
    public static let warningAmber = Color(red: 0.95, green: 0.60, blue: 0.15) // #F29927
    
    // MARK: - Surfaces & Backgrounds (Glanceable Light Mode)
    /// Clean pure white background for main watch views
    public static let backgroundPrimary = Color.white
    /// Subtle off-white background for grouped views and lists
    public static let backgroundSecondary = Color(red: 0.96, green: 0.97, blue: 0.98) // #F5F7FA
    /// Light grey surface used for cards, pill buttons, and interactive chips
    public static let cardBackground = Color(red: 0.93, green: 0.94, blue: 0.96) // #EDEFE5 / #EEF0F4
    /// Highlighted surface for elevated containers
    public static let cardBackgroundElevated = Color(red: 0.89, green: 0.91, blue: 0.93)
    
    // MARK: - Typography & High-Contrast Strokes
    /// High-contrast black for addresses and critical glanceable headers
    public static let textPrimary = Color(red: 0.07, green: 0.08, blue: 0.09) // #121417
    /// Muted slate for metadata like package IDs, vehicle labels, and secondary hints
    public static let textSecondary = Color(red: 0.40, green: 0.44, blue: 0.50) // #667080
    /// Tertiary text for ultra-compact subtitles
    public static let textTertiary = Color(red: 0.58, green: 0.62, blue: 0.68)
    
    // MARK: - Borders & Dividers
    /// Subtle boundary for card containers and outline buttons
    public static let borderSubtle = Color(red: 0.84, green: 0.86, blue: 0.89)
    /// High-contrast border for interactive focus
    public static let borderStrong = Color(red: 0.70, green: 0.73, blue: 0.78)
    
    // MARK: - Map & Route Visuals
    /// Route trajectory stroke color on mini map
    public static let routeLine = Color(red: 0.13, green: 0.69, blue: 0.38)
    /// Waypoint pin stroke
    public static let waypointBorder = Color.white
    /// Waypoint pin fill
    public static let waypointFill = Color(red: 0.13, green: 0.69, blue: 0.38)
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
