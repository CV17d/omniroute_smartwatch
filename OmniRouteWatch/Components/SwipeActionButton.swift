import SwiftUI

// MARK: - SwipeActionButton Component
/// A swipe-to-confirm interactive component preventing accidental triggers
/// for critical actions (such as emergency roadside assistance or skipping stops).
public struct SwipeActionButton: View {
    public let title: String
    public let icon: String
    public let tintColor: Color
    public let onConfirm: () -> Void
    
    @State private var dragOffset: CGFloat = 0
    @State private var isConfirmed: Bool = false
    
    public init(
        title: String = "Deslizar para saltar",
        icon: String = "chevron.right.2",
        tintColor: Color = OmniColors.alertRed,
        onConfirm: @escaping () -> Void
    ) {
        self.title = title
        self.icon = icon
        self.tintColor = tintColor
        self.onConfirm = onConfirm
    }
    
    public var body: some View {
        GeometryReader { geometry in
            let maxDrag = geometry.size.width - 44
            
            ZStack(alignment: .leading) {
                // Background track
                RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                    .fill(OmniColors.cardBackground)
                    .overlay(
                        RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                            .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
                    )
                
                // Track fill
                RoundedRectangle(cornerRadius: OmniLayout.radiusPill)
                    .fill(tintColor.opacity(0.18))
                    .frame(width: max(44, dragOffset + 44))
                
                // Title hint
                Text(title)
                    .font(OmniTypography.secondaryButton)
                    .foregroundColor(OmniColors.textSecondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.leading, 32)
                
                // Draggable knob
                Circle()
                    .fill(tintColor)
                    .frame(width: 40, height: 40)
                    .overlay(
                        Image(systemName: icon)
                            .font(.system(size: 13, weight: .black))
                            .foregroundColor(.white)
                    )
                    .offset(x: dragOffset + 2)
                    .gesture(
                        DragGesture()
                            .onChanged { value in
                                if !isConfirmed {
                                    dragOffset = min(max(0, value.translation.width), maxDrag)
                                }
                            }
                            .onEnded { value in
                                if dragOffset >= maxDrag * 0.75 {
                                    withAnimation(.spring()) {
                                        dragOffset = maxDrag
                                        isConfirmed = true
                                    }
                                    onConfirm()
                                } else {
                                    withAnimation(.spring()) {
                                        dragOffset = 0
                                    }
                                }
                            }
                    )
            }
        }
        .frame(height: OmniLayout.primaryButtonHeight)
    }
}

// MARK: - SwipeActionButton Previews
#Preview("SwipeActionButton Previews") {
    VStack(spacing: 12) {
        SwipeActionButton(title: "Deslizar para saltar") {}
        SwipeActionButton(title: "Alerta de Emergencia", icon: "bolt.fill", tintColor: OmniColors.warningAmber) {}
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
