import 'package:flutter/material.dart';

/// Centers child content with a max width on wide screens.
/// On phones (< maxWidth) it just adds horizontal padding.
class MaxWidthContent extends StatelessWidget {
  final Widget child;
  final double maxWidth;
  final EdgeInsetsGeometry padding;

  const MaxWidthContent({
    super.key,
    required this.child,
    this.maxWidth = 1200,
    this.padding = const EdgeInsets.symmetric(horizontal: 16),
  });

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: Alignment.topCenter,
      child: ConstrainedBox(
        constraints: BoxConstraints(maxWidth: maxWidth),
        child: Padding(padding: padding, child: child),
      ),
    );
  }
}
