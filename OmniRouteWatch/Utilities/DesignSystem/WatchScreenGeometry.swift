import SwiftUI
import WatchKit

// MARK: - WatchScreenGeometry
/// Adaptive screen size geometry detector ensuring touch targets and paddings
/// automatically scale across 40mm, 41mm, 44mm, 45mm (Series 9) and 49mm (Ultra).
public enum WatchScreenGeometry {
    public static var screenBounds: CGRect {
        #if os(watchOS)
        return WKInterfaceDevice.current().screenBounds
        #else
        return CGRect(x: 0, y: 0, width: 198, height: 242)
        #endif
    }
    
    public static var isUltra: Bool {
        screenBounds.width >= 205 // Apple Watch Ultra width ~ 205pt
    }
    
    public static var isCompact: Bool {
        screenBounds.width <= 180 // 40mm / 41mm screen
    }
    
    /// Adaptive horizontal padding for container edges
    public static var horizontalEdgePadding: CGFloat {
        if isUltra {
            return 14
        } else if isCompact {
            return 8
        } else {
            return 10
        }
    }
    
    /// Adaptive primary button height
    public static var primaryButtonHeight: CGFloat {
        isUltra ? 50 : 46
    }
    
    /// Adaptive address font size
    public static var addressFontSize: CGFloat {
        isUltra ? 18 : (isCompact ? 15 : 17)
    }
}

// MARK: - View Adaptive Screen Modifier
public struct AdaptiveScreenPaddingModifier: ViewModifier {
    public func body(content: Content) -> some View {
        content
            .padding(.horizontal, WatchScreenGeometry.horizontalEdgePadding)
    }
}

extension View {
    public func adaptiveScreenPadding() -> some View {
        self.modifier(AdaptiveScreenPaddingModifier())
    }
}
