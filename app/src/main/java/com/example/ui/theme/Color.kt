package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Core Obsidian & Aurora Glass Palette
val ObsidianBlack = Color(0xFF06080F)
val DeepSpaceSurface = Color(0xFF0C111E)
val ElevatedSpaceSurface = Color(0xFF141C2F)
val FrostedLightSurface = Color(0xFFF3F6FC)
val PureIceSurface = Color(0xFFFFFFFF)
val SoftIceVariant = Color(0xFFE4EAF7)

val AuroraCyan = Color(0xFF22D3EE)
val AuroraCyanDark = Color(0xFF0891B2)
val ElectricViolet = Color(0xFF8B5CF6)
val ElectricVioletDark = Color(0xFF6D28D9)
val NeonEmerald = Color(0xFF10B981)
val AmberPulse = Color(0xFFF59E0B)
val CoralAlert = Color(0xFFF43F5E)

val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)

// Preset color options for user personal customization (ARGB Long values)
object NovaColorPresets {
    val NotchBackgrounds = listOf(
        0xFF05070B to "Obsidian Pure",
        0xFF0F172A to "Midnight Slate",
        0xFF1B1035 to "Nebula Violet",
        0xFF08202B to "Deep Abyss",
        0xFF1F1218 to "Crimson Velvet",
        0xFF0C221A to "Emerald Shadow"
    )

    val DrawerTints = listOf(
        0xFF0F172A to "Frosted Slate",
        0xFF1E1B4B to "Aurora Indigo",
        0xFF083344 to "Glacier Cyan",
        0xFF2E1065 to "Cosmic Plum",
        0xFF064E3B to "Jade Mist",
        0xFF18181B to "Graphite Glass"
    )

    val AccentGlows = listOf(
        0xFF22D3EE to "Aurora Cyan",
        0xFF8B5CF6 to "Electric Violet",
        0xFF38BDF8 to "iOS Sky Blue",
        0xFF10B981 to "Neon Emerald",
        0xFFF43F5E to "Coral Pulse",
        0xFFF59E0B to "Solar Amber"
    )
}
