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
            
            # replace 'if (mounted' with 'if (context.mounted'
            new_content = re.sub(r'\bif\s*\(\s*mounted\b', 'if (context.mounted', new_content)
            # replace 'if (!mounted' with 'if (!context.mounted'
            new_content = re.sub(r'\bif\s*\(\s*!mounted\b', 'if (!context.mounted', new_content)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed {filepath}")

if __name__ == '__main__':
    fix_files('rental-app-flutter/lib')
