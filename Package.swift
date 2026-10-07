// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "OmniRouteWatch",
    platforms: [
        .watchOS(.v9),
        .iOS(.v16),
        .macOS(.v13)
    ],
    products: [
        .library(
            name: "OmniRouteWatch",
            targets: ["OmniRouteWatch"]
        ),
    ],
    dependencies: [],
    targets: [
        .target(
            name: "OmniRouteWatch",
            dependencies: [],
            path: "OmniRouteWatch",
            exclude: ["Tests", "README.md"]
        ),
        .testTarget(
            name: "OmniRouteWatchTests",
            dependencies: ["OmniRouteWatch"],
            path: "OmniRouteWatch/Tests"
        ),
    ]
)
