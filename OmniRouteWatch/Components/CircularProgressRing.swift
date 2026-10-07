import SwiftUI

// MARK: - CircularProgressRing Component
/// A mini circular progress ring indicating current delivery route progress
/// (e.g. 4/15 stops completed), as displayed in the overview card of Watch 1.
public struct CircularProgressRing: View {
    public let progress: Double // Range 0.0 ... 1.0
    public let ringDiameter: CGFloat
    public let lineWidth: CGFloat
    public let progressColor: Color
    public let trackColor: Color
    
    public init(
        progress: Double,
        ringDiameter: CGFloat = OmniLayout.progressRingSmall,
        lineWidth: CGFloat = 2.5,
        progressColor: Color = OmniColors.deliveryGreen,
        trackColor: Color = OmniColors.borderSubtle
    ) {
        self.progress = min(max(progress, 0.0), 1.0)
        self.ringDiameter = ringDiameter
        self.lineWidth = lineWidth
        self.progressColor = progressColor
        self.trackColor = trackColor
    }
    
    public var body: some View {
        ZStack {
            // Track circle
            Circle()
                .stroke(trackColor, lineWidth: lineWidth)
            
            // Progress arc
            Circle()
                .trim(from: 0.0, to: CGFloat(progress))
                .stroke(
                    progressColor,
                    style: StrokeStyle(lineWidth: lineWidth, lineCap: .round)
                )
                .rotationEffect(.degrees(-90))
                .animation(.easeOut(duration: 0.35), value: progress)
        }
        .frame(width: ringDiameter, height: ringDiameter)
    }
}

// MARK: - CircularProgressRing Previews
#Preview("CircularProgressRing Variants") {
    HStack(spacing: 16) {
        CircularProgressRing(progress: 4.0 / 15.0)
        CircularProgressRing(progress: 0.5, ringDiameter: 24, lineWidth: 3.5)
        CircularProgressRing(progress: 1.0, ringDiameter: 28, lineWidth: 4.0)
    }
    .padding()
    .background(OmniColors.backgroundPrimary)
}
