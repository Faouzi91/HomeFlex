import os
import re

def fix_files(directory):
    for root, _, files in os.walk(directory):
        for file in files:
            if not file.endswith('.dart'):
                continue
            
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            new_content = content
            
            # Revert the incorrect context.mounted back to mounted
            new_content = re.sub(r'\bif\s*\(\s*context\.mounted\b', 'if (mounted', new_content)
            new_content = re.sub(r'\bif\s*\(\s*!context\.mounted\b', 'if (!mounted', new_content)

            # Now, for the specific early return pattern the linter wants for async context usage in State:
            # 1. login_screen.dart line 117: if (mounted && ref.read(authProvider).user != null)
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.user\s*!=\s*null\s*\)\s*\{\s*context\.go\(\'/properties\'\);\s*\}',
                r'if (!mounted) return;\n                              if (ref.read(authProvider).user != null) {\n                                context.go(\'/properties\');\n                              }',
                new_content
            )
            # login_screen.dart line 38 (actually _signIn method):
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.user\s*!=\s*null\s*\)\s*\{\s*context\.go\(\'/properties\'\);\s*\}',
                r'if (!mounted) return;\n      if (ref.read(authProvider).user != null) {\n        context.go(\'/properties\');\n      }',
                new_content
            )

            # 2. register_screen.dart line 147:
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.user\s*!=\s*null\s*\)\s*\{\s*context\.go\(\'/properties\'\);\s*\}',
                r'if (!mounted) return;\n                              if (ref.read(authProvider).user != null) {\n                                context.go(\'/properties\');\n                              }',
                new_content
            )

            # 3. reset_password_screen.dart:
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.message\s*!=\s*null\s*\)\s*\{\s*ScaffoldMessenger\.of\(context\)\.showSnackBar',
                r'if (!mounted) return;\n                              if (ref.read(authProvider).message != null) {\n                                ScaffoldMessenger.of(context).showSnackBar',
                new_content
            )
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*\)\s*context\.go\(\'/login\'\);',
                r'if (!mounted) return;\n                              context.go(\'/login\');',
                new_content
            )

            # 4. change_password_screen.dart:
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.error\s*==\s*null\s*\)\s*\{\s*ScaffoldMessenger\.of\(context\)\.showSnackBar',
                r'if (!mounted) return;\n                              if (ref.read(authProvider).error == null) {\n                                ScaffoldMessenger.of(context).showSnackBar',
                new_content
            )
            new_content = re.sub(
                r'\}\s*if\s*\(\s*mounted\s*\)\s*context\.pop\(\);',
                r'}\n                              if (!mounted) return;\n                              context.pop();',
                new_content
            )

            # 5. edit_profile_screen.dart:
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*&&\s*ref\.read\(authProvider\)\.error\s*==\s*null\s*\)\s*\{\s*ScaffoldMessenger\.of\(context\)\.showSnackBar',
                r'if (!mounted) return;\n                              if (ref.read(authProvider).error == null) {\n                                ScaffoldMessenger.of(context).showSnackBar',
                new_content
            )

            # 6. property_detail_screen.dart:
            new_content = re.sub(
                r'if\s*\(\s*mounted\s*\)\s*\{\s*ScaffoldMessenger\.of\(context\)\.showSnackBar',
                r'if (!mounted) return;\n                              ScaffoldMessenger.of(context).showSnackBar',
                new_content
            )
            
            # fix for ctx.mounted inside edit_profile_screen/property_detail_screen closures
            new_content = re.sub(
                r'if\s*\(\s*ctx\.mounted\s*\)\s*Navigator\.pop\(ctx\);',
                r'if (!ctx.mounted) return;\n                  Navigator.pop(ctx);',
                new_content
            )

            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed {filepath}")

if __name__ == '__main__':
    fix_files('rental-app-flutter/lib')
