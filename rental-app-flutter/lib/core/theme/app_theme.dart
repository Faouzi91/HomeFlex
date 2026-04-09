import 'package:flutter/material.dart';

/// HomeFlex design system. Premium, editorial, confident.
///
/// Palette anchored on a deep emerald (trust + nature) with warm coral
/// accent for CTAs. Generous radii, soft shadows, no Material elevations.
class AppTheme {
  // Brand
  static const brandPrimary = Color(0xFF0E6E5C); // deep emerald
  static const brandPrimaryDark = Color(0xFF0A4F42);
  static const brandAccent = Color(0xFFFF5A5F); // signature coral
  static const brandSurface = Color(0xFFFFFBF7); // warm off-white
  static const brandInk = Color(0xFF0F1B2D); // near-black ink
  static const brandMuted = Color(0xFF6B7280); // neutral gray

  static ThemeData light() {
    final colorScheme = ColorScheme(
      brightness: Brightness.light,
      primary: brandPrimary,
      onPrimary: Colors.white,
      primaryContainer: const Color(0xFFCCEDE4),
      onPrimaryContainer: brandPrimaryDark,
      secondary: brandAccent,
      onSecondary: Colors.white,
      secondaryContainer: const Color(0xFFFFE3E4),
      onSecondaryContainer: const Color(0xFF7A1F22),
      tertiary: const Color(0xFFE8A33D),
      onTertiary: Colors.white,
      tertiaryContainer: const Color(0xFFFFEACB),
      onTertiaryContainer: const Color(0xFF6B4400),
      error: const Color(0xFFD92D20),
      onError: Colors.white,
      errorContainer: const Color(0xFFFEE4E2),
      onErrorContainer: const Color(0xFF7A271A),
      surface: brandSurface,
      onSurface: brandInk,
      surfaceContainerHighest: const Color(0xFFF1EDE7),
      onSurfaceVariant: brandMuted,
      outline: const Color(0xFFD0D5DD),
      outlineVariant: const Color(0xFFE4E7EC),
      shadow: Colors.black,
      scrim: Colors.black,
      inverseSurface: brandInk,
      onInverseSurface: brandSurface,
      inversePrimary: const Color(0xFF7DDFCB),
      surfaceTint: brandPrimary,
    );
    return _buildTheme(colorScheme);
  }

  static ThemeData dark() {
    final colorScheme = ColorScheme(
      brightness: Brightness.dark,
      primary: const Color(0xFF7DDFCB),
      onPrimary: const Color(0xFF003830),
      primaryContainer: brandPrimaryDark,
      onPrimaryContainer: const Color(0xFFCCEDE4),
      secondary: const Color(0xFFFF8A8E),
      onSecondary: const Color(0xFF5C0008),
      secondaryContainer: const Color(0xFF7A1F22),
      onSecondaryContainer: const Color(0xFFFFE3E4),
      tertiary: const Color(0xFFFFC97A),
      onTertiary: const Color(0xFF402900),
      tertiaryContainer: const Color(0xFF6B4400),
      onTertiaryContainer: const Color(0xFFFFEACB),
      error: const Color(0xFFFF8A80),
      onError: const Color(0xFF5C0008),
      errorContainer: const Color(0xFF7A271A),
      onErrorContainer: const Color(0xFFFEE4E2),
      surface: const Color(0xFF0F1419),
      onSurface: const Color(0xFFE9EDF1),
      surfaceContainerHighest: const Color(0xFF1A2027),
      onSurfaceVariant: const Color(0xFF9CA3AF),
      outline: const Color(0xFF374151),
      outlineVariant: const Color(0xFF1F2937),
      shadow: Colors.black,
      scrim: Colors.black,
      inverseSurface: brandSurface,
      onInverseSurface: brandInk,
      inversePrimary: brandPrimary,
      surfaceTint: const Color(0xFF7DDFCB),
    );
    return _buildTheme(colorScheme);
  }

  static ThemeData _buildTheme(ColorScheme colorScheme) {
    final isLight = colorScheme.brightness == Brightness.light;

    final baseTextTheme = (isLight ? ThemeData.light() : ThemeData.dark())
        .textTheme
        .apply(
          bodyColor: colorScheme.onSurface,
          displayColor: colorScheme.onSurface,
        );

    final textTheme = baseTextTheme.copyWith(
      displayLarge: baseTextTheme.displayLarge?.copyWith(
        fontWeight: FontWeight.w800,
        letterSpacing: -1.5,
        height: 1.05,
      ),
      displayMedium: baseTextTheme.displayMedium?.copyWith(
        fontWeight: FontWeight.w800,
        letterSpacing: -1.2,
        height: 1.05,
      ),
      displaySmall: baseTextTheme.displaySmall?.copyWith(
        fontWeight: FontWeight.w800,
        letterSpacing: -0.8,
        height: 1.1,
      ),
      headlineLarge: baseTextTheme.headlineLarge?.copyWith(
        fontWeight: FontWeight.w700,
        letterSpacing: -0.6,
      ),
      headlineMedium: baseTextTheme.headlineMedium?.copyWith(
        fontWeight: FontWeight.w700,
        letterSpacing: -0.4,
      ),
      headlineSmall: baseTextTheme.headlineSmall?.copyWith(
        fontWeight: FontWeight.w700,
        letterSpacing: -0.2,
      ),
      titleLarge: baseTextTheme.titleLarge?.copyWith(fontWeight: FontWeight.w700),
      titleMedium: baseTextTheme.titleMedium?.copyWith(fontWeight: FontWeight.w600),
      labelLarge: baseTextTheme.labelLarge?.copyWith(
        fontWeight: FontWeight.w600,
        letterSpacing: 0.2,
      ),
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: colorScheme.surface,
      textTheme: textTheme,
      splashFactory: InkSparkle.splashFactory,
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: isLight
            ? Colors.white
            : colorScheme.surfaceContainerHighest,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(color: colorScheme.outlineVariant),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(color: colorScheme.outlineVariant),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(14),
          borderSide: BorderSide(color: colorScheme.primary, width: 1.5),
        ),
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
      ),
      cardTheme: CardThemeData(
        clipBehavior: Clip.antiAlias,
        elevation: 0,
        color: isLight ? Colors.white : colorScheme.surfaceContainerHighest,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
          side: BorderSide(color: colorScheme.outlineVariant, width: 1),
        ),
      ),
      appBarTheme: AppBarTheme(
        centerTitle: false,
        elevation: 0,
        scrolledUnderElevation: 0,
        surfaceTintColor: Colors.transparent,
        backgroundColor: colorScheme.surface,
        foregroundColor: colorScheme.onSurface,
        titleTextStyle: textTheme.titleLarge,
      ),
      navigationBarTheme: NavigationBarThemeData(
        indicatorColor: colorScheme.primaryContainer,
        backgroundColor: colorScheme.surface,
        elevation: 0,
        height: 68,
        labelBehavior: NavigationDestinationLabelBehavior.alwaysShow,
      ),
      navigationRailTheme: NavigationRailThemeData(
        backgroundColor: colorScheme.surface,
        indicatorColor: colorScheme.primaryContainer,
        selectedIconTheme: IconThemeData(color: colorScheme.primary),
        selectedLabelTextStyle: TextStyle(
          color: colorScheme.primary,
          fontWeight: FontWeight.w600,
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(14),
          ),
          textStyle: const TextStyle(
            fontWeight: FontWeight.w600,
            fontSize: 15,
            letterSpacing: 0.2,
          ),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          side: BorderSide(color: colorScheme.outline),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(14),
          ),
          textStyle: const TextStyle(
            fontWeight: FontWeight.w600,
            fontSize: 15,
          ),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          textStyle: const TextStyle(fontWeight: FontWeight.w600),
        ),
      ),
      chipTheme: ChipThemeData(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(999),
          side: BorderSide(color: colorScheme.outlineVariant),
        ),
        side: BorderSide(color: colorScheme.outlineVariant),
        labelStyle: const TextStyle(fontWeight: FontWeight.w500),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      ),
      dividerTheme: DividerThemeData(
        color: colorScheme.outlineVariant,
        space: 1,
        thickness: 1,
      ),
      snackBarTheme: SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
      ),
    );
  }
}
