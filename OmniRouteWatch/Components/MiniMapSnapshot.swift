import SwiftUI

// MARK: - MiniMapSnapshot View
/// Vectorized lightweight route map matching the visual presentation of Watch 1.
/// Renders terrain, street networks, green delivery route polyline, and stop markers
/// with negligible CPU and battery consumption.
public struct MiniMapSnapshot: View {
    public let waypoints: [GeoCoordinate]
    public let activeStopIndex: Int
    
    public init(
        waypoints: [GeoCoordinate] = [],
        activeStopIndex: Int = 3
    ) {
        self.waypoints = waypoints
        self.activeStopIndex = activeStopIndex
    }
    
    public var body: some View {
        GeometryReader { geometry in
            let width = geometry.size.width
            let height = geometry.size.height
            
            ZStack {
                // Map Background (Land mass)
                Color(red: 0.95, green: 0.94, blue: 0.91)
                
                // Stylized Water / Coastline shapes (similar to reference image)
                Path { path in
                    path.move(to: CGPoint(x: 0, y: 0))
                    path.addLine(to: CGPoint(x: width * 0.28, y: 0))
                    path.addQuadCurve(
                        to: CGPoint(x: 0, y: height * 0.65),
                        control: CGPoint(x: width * 0.15, y: height * 0.32)
                    )
                    path.closeSubpath()
                }
                .fill(Color(red: 0.72, green: 0.86, blue: 0.93)) // Soft coastal blue
                
                Path { path in
                    path.move(to: CGPoint(x: width * 0.85, y: 0))
                    path.addLine(to: CGPoint(x: width, y: 0))
                    path.addLine(to: CGPoint(x: width, y: height * 0.45))
                    path.addQuadCurve(
                        to: CGPoint(x: width * 0.85, y: 0),
                        control: CGPoint(x: width * 0.88, y: height * 0.20)
                    )
                    path.closeSubpath()
                }
                .fill(Color(red: 0.72, green: 0.86, blue: 0.93))
                
                // Street grid vectors
                Path { path in
                    // Diagonal secondary roads
                    path.move(to: CGPoint(x: width * 0.25, y: height * 0.9))
                    path.addLine(to: CGPoint(x: width * 0.85, y: height * 0.35))
                    
                    path.move(to: CGPoint(x: width * 0.35, y: height))
                    path.addLine(to: CGPoint(x: width * 0.95, y: height * 0.45))
                    
                    path.move(to: CGPoint(x: width * 0.15, y: height * 0.6))
                    path.addLine(to: CGPoint(x: width * 0.65, y: height * 0.15))
                    
                    path.move(to: CGPoint(x: width * 0.5, y: height * 0.95))
                    path.addLine(to: CGPoint(x: width * 0.3, y: height * 0.45))
                }
                .stroke(Color.white, lineWidth: 2.5)
                
                // Green Delivery Route Polyline (loop circuit as in reference image)
                Path { path in
                    path.move(to: CGPoint(x: width * 0.48, y: height * 0.78))
                    path.addLine(to: CGPoint(x: width * 0.42, y: height * 0.62))
                    path.addLine(to: CGPoint(x: width * 0.35, y: height * 0.45))
                    path.addLine(to: CGPoint(x: width * 0.32, y: height * 0.25))
                    path.addQuadCurve(
                        to: CGPoint(x: width * 0.42, y: height * 0.28),
                        control: CGPoint(x: width * 0.36, y: height * 0.18)
                    )
                    path.addLine(to: CGPoint(x: width * 0.55, y: height * 0.46))
                    path.addLine(to: CGPoint(x: width * 0.72, y: height * 0.38))
                    path.addQuadCurve(
                        to: CGPoint(x: width * 0.72, y: height * 0.20),
                        control: CGPoint(x: width * 0.80, y: height * 0.26)
                    )
                }
                .stroke(
                    OmniColors.routeLine,
                    style: StrokeStyle(lineWidth: 3.5, lineCap: .round, lineJoin: .round)
                )
                
                // Waypoint Markers
                Group {
                    // Waypoint 1 (Top loop)
                    WaypointPin(center: CGPoint(x: width * 0.33, y: height * 0.25))
                    
                    // Waypoint 2 (Mid loop)
                    WaypointPin(center: CGPoint(x: width * 0.36, y: height * 0.46))
                    
                    // Waypoint 3 (Lower route)
                    WaypointPin(center: CGPoint(x: width * 0.42, y: height * 0.62))
                    
                    // Waypoint 4 (Top right)
                    WaypointPin(center: CGPoint(x: width * 0.72, y: height * 0.20))
                    
                    // Waypoint 5 (Mid right)
                    WaypointPin(center: CGPoint(x: width * 0.72, y: height * 0.38))
                    
                    // Waypoint 6 (Mid connector)
                    WaypointPin(center: CGPoint(x: width * 0.55, y: height * 0.46))
                }
                
                // Active Current Stop Pin (Location beacon with pin head)
                ActiveCourierPin(center: CGPoint(x: width * 0.48, y: height * 0.78))
            }
            .clipShape(RoundedRectangle(cornerRadius: OmniLayout.radiusCard))
            .overlay(
                RoundedRectangle(cornerRadius: OmniLayout.radiusCard)
                    .stroke(OmniColors.borderSubtle, lineWidth: OmniLayout.borderHairline)
            )
        }
    }
}

// MARK: - Waypoint Node
private struct WaypointPin: View {
    let center: CGPoint
    
    var body: some View {
        Circle()
            .fill(OmniColors.waypointFill)
            .frame(width: 8, height: 8)
            .overlay(
                Circle()
                    .stroke(Color.white, lineWidth: 1.5)
            )
            .position(center)
    }
}

// MARK: - Active Courier Pin
private struct ActiveCourierPin: View {
    let center: CGPoint
    
    var body: some View {
        ZStack {
            // Pulse ring
            Circle()
                .fill(OmniColors.deliveryGreen.opacity(0.3))
                .frame(width: 16, height: 16)
            
            // Pin head
            Image(systemName: "mappin.circle.fill")
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(OmniColors.deliveryGreenDark)
                .background(Circle().fill(Color.white).frame(width: 10, height: 10))
        }
        .position(center)
    }
}

// MARK: - MiniMapSnapshot Previews
#Preview("MiniMapSnapshot Preview") {
    MiniMapSnapshot()
        .frame(height: 140)
        .padding()
        .background(OmniColors.backgroundPrimary)
}
