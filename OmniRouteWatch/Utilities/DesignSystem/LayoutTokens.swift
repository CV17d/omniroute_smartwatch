import SwiftUI

// MARK: - OmniRoute Layout & Dimension Tokens
/// Layout constants and geometry specs strictly adhering to watchOS Human Interface Guidelines
/// ensuring 44pt+ minimum touch targets, comfortable wrist tap ergonomics, and visual hierarchy.
public enum OmniLayout {
    // MARK: - Spacing Scale
    public static let spacingXXSmall: CGFloat = 2
    public static let spacingXSmall: CGFloat = 4
    public static let spacingSmall: CGFloat = 8
    public static let spacingMedium: CGFloat = 12
    public static let spacingLarge: CGFloat = 16
    public static let spacingXLarge: CGFloat = 20
    
    // MARK: - Corner Radii
    /// Pill shape radius for primary action buttons (e.g., "INICIAR RUTA", "¡LLEGUE!")
    public static let radiusPill: CGFloat = 28
    /// Container radius for cards and modal wrappers
    public static let radiusCard: CGFloat = 18
    /// Subtle rounding for chips, badges, and status pills
    public static let radiusChip: CGFloat = 10
    public static let radiusSmall: CGFloat = 6
    
    // MARK: - Component Heights (Watch Touch Targets)
    /// Primary high-priority button height (massive target for gloved or moving hands)
    public static let primaryButtonHeight: CGFloat = 46
    /// Secondary action button height
    public static let secondaryButtonHeight: CGFloat = 38
    /// Top instruction header banner height
    public static let instructionHeaderHeight: CGFloat = 36
    /// Circular progress ring diameter
    public static let progressRingSmall: CGFloat = 16
    public static let progressRingMedium: CGFloat = 28
    
    // MARK: - Border Widths
    public static let borderHairline: CGFloat = 0.5
    public static let borderSubtle: CGFloat = 1.0
    public static let borderEmphasis: CGFloat = 2.0
}
